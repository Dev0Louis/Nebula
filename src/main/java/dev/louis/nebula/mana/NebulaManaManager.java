package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.ManaManager;
import dev.louis.nebula.api.mana.ManaSource;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
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

import static dev.louis.nebula.Nebula.MANA_NBT_KEY;

@ApiStatus.Internal
public class NebulaManaManager extends SnapshotParticipant<Float> implements ManaManager  {
    private static final float MANA_REGEN_RATE = 0.005f;
    protected LivingEntity entity;
    protected float mana;
    protected float lastSyncedMana = -1;
    private boolean changed;
    private Collection<ManaSource> alternativeManaSources;

    public NebulaManaManager(LivingEntity entity, Collection<ManaSource> alternativeManaSources) {
        this.entity = entity;
        this.alternativeManaSources = alternativeManaSources;
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

    @Override
    public void setMana(float mana) {
        this.mana = (Math.max(Math.min(mana, this.getCapacity()), 0));
        this.checkSync();
    }

    public void setMana(float mana, TransactionContext context) {
        this.setMana(mana, this.needsSyncing(), context);
    }

    public void setMana(float mana, boolean syncToClient, TransactionContext context) {
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

    @Override
    public float extractMana(float requestedExtraction, TransactionContext context) {
        if (requestedExtraction < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        float extraction = extractAlternatives(Math.min(requestedExtraction, this.mana), requestedExtraction, context);


        // implicit NaN check (as NaN > 0 = false)
        var shouldExtract = extraction > 0;

        if (shouldExtract) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
            return extraction;
        }

        return 0;
    }

    private float extractAlternatives(float extraction, float requestedExtraction, TransactionContext context) {
        float extractedMana = extraction;
        for (ManaSource alternativeManaSource : alternativeManaSources) {
            var toExtract = requestedExtraction - extractedMana;

            if (toExtract < 0) throw new IllegalStateException("toExtract should never be < 0");

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
                if (syncMana == this.lastSyncedMana) return true;
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

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat(MANA_NBT_KEY, this.getMana());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.setMana(nbt.getFloat(MANA_NBT_KEY));
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
