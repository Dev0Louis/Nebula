package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.manager.ManaManager;
import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.entrypoint.AlternativeManaSourceRegistererImpl;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import dev.louis.nebula.util.Phase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

import static dev.louis.nebula.constants.NbtConstants.MANA;

@ApiStatus.Internal
public class NebulaManaManager extends SnapshotParticipant<Float> implements ManaManager  {
    private static final float MANA_REGEN_RATE = 1/60f;
    protected LivingEntity entity;
    protected float mana;
    protected float lastSyncedMana = -1;
    private boolean changed;
    
    private final Collection<ManaSource> alternativePreManaSources;
    private final Collection<ManaSource> alternativePostManaSources;

    public NebulaManaManager(LivingEntity entity, Collection<ManaSource> alternativePreManaSources, Collection<ManaSource> alternativePostManaSources) {
        this.entity = entity;
        this.alternativePreManaSources = alternativePreManaSources;
        this.alternativePostManaSources = alternativePostManaSources;
        mana = this.getCapacity();
    }

    public static NebulaManaManager createManaManager(LivingEntity entity) {
        return new NebulaManaManager(
                entity,
                AlternativeManaSourceRegistererImpl.INSTANCE.createManaSourcesFor(entity, Phase.PRE),
                AlternativeManaSourceRegistererImpl.INSTANCE.createManaSourcesFor(entity, Phase.POST)
        );
    }

    public void tick() {
        if (shouldRegenMana()) this.regenMana();
        var capacity = getCapacity();
        if (mana > capacity) {
            this.mana = capacity;
            this.checkSync();
        }

        if (this.changed) {
            this.sendSync();
            this.changed = false;
        }
    }

    @Override
    public float getCapacity() {
        return entity.isMobOrPlayer() ? entity.getMaxHealth() : 0;
    }

    public void regenMana() {
        try(Transaction tx = Transaction.openOuter()) {
            insertMana(MANA_REGEN_RATE, tx);
            tx.commit();
        }
    }

    private boolean shouldRegenMana() {
        return !this.entity.getWorld().isClient() && this.entity.isMobOrPlayer() && this.entity.isAlive();
    }

    public float getMana() {
        return mana;
    }

    public void setManaUnsynced(float unsyncedMana) {
        this.mana = (Math.max(Math.min(unsyncedMana, this.getCapacity()), 0));
    }

    @Override
    public void setMana(float mana) {
        setManaUnsynced(mana);
        this.checkSync();
    }

    public void setMana(float mana, TransactionContext context) {
        var newMana = Math.max(Math.min(mana, this.getCapacity()), 0);
        if (newMana != this.mana) {
            updateSnapshots(context);
            this.mana = newMana;
        }
    }

    @Override
    public float insertMana(float amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        float insertion = Math.min(amount, this.getCapacity() - mana);

        // implicit NaN check (as NaN > 0 = false)
        var shouldInsert = insertion > 0;

        if (shouldInsert) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
            return insertion;
        }

        return 0;
    }

    // Very sane code ;v;
    @Override
    public float extractMana(float requestedExtraction, TransactionContext context) {
        if (requestedExtraction < 0) throw new IllegalArgumentException("Extraction amount is negative.");

        // This local is going to get modified throughout this code and will be returned at the end.
        float extraction = extractAlternative(requestedExtraction, context, Phase.PRE);
        var mainRequestedExtraction = requestedExtraction - extraction;


        float mainExtraction = Math.min(mainRequestedExtraction, this.mana);

        // implicit NaN check (as NaN > 0 = false)
        var shouldExtractMain = mainExtraction > 0;

        if (shouldExtractMain) {
            updateSnapshots(context);

            this.mana = this.mana - mainExtraction;
            extraction += mainExtraction;
        }

        var postRequestedExtraction = requestedExtraction - extraction;
        extraction += extractAlternative(postRequestedExtraction, context, Phase.POST);

        return extraction;
    }

    private float extractAlternative(float requestedExtraction, TransactionContext context, Phase phase) {
        float extractedMana = 0;
        for (ManaSource alternativeManaSource : switch (phase) {
                case PRE -> alternativePreManaSources;
                case POST -> alternativePostManaSources;
            }) {
            var toExtract = requestedExtraction - extractedMana;

            if (toExtract < 0) throw new IllegalStateException("toExtract should never be < 0. It is " + toExtract + "!");

            if (toExtract == 0) break;

            extractedMana += alternativeManaSource.extractMana(toExtract, context);
        }

        return extractedMana;
    }



    @Override
    protected void onFinalCommit() {
        this.checkSync();
    }

    public void checkSync() {
        this.changed = this.needsSyncing();
    }

    public boolean sendSync() {
        if (this.entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.networkHandler != null) {
                float syncMana = this.getMana();
                this.lastSyncedMana = syncMana;
                ServerPlayNetworking.send(serverPlayerEntity, new SyncManaPayload(syncMana));
                return true;
            }
            //Hm didn't work
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("resource")
    public static void receive(SyncManaPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> context.player().getManaManager().setMana(payload.mana()));
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("resource")
    public static void receive(StartSpellEffectPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> context.player().startSpellEffect(payload.getSpellEffect()));
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("resource")
    public static void receive(StopSpellEffectPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> context.player().stopSpellEffect(payload.getSpellEffect()));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat(MANA, this.getMana());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.setMana(nbt.getFloat(MANA));
    }

    public void copyFrom(ManaManager manaManager) {
        this.setMana(manaManager.getMana());
    }

    @Override
    protected Float createSnapshot() {
        return mana;
    }

    @Override
    protected void readSnapshot(Float snapshot) {
        this.mana = snapshot;
    }

    public boolean needsSyncing() {
        return entity instanceof ServerPlayerEntity && lastSyncedMana != this.mana;
    }
}
