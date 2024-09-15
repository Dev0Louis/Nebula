package dev.louis.nebula.mana;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.event.ManaExtractionCallback;
import dev.louis.nebula.api.event.ManaInsertionCallback;
import dev.louis.nebula.api.mana.ExtractionContext;
import dev.louis.nebula.api.mana.InsertionContext;
import dev.louis.nebula.api.mana.ManaManager;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import static dev.louis.nebula.Nebula.MANA_NBT_KEY;

@ApiStatus.Internal
public class NebulaManaManager extends SnapshotParticipant<Float> implements ManaManager  {
    protected LivingEntity entity;
    protected float mana;
    protected float lastSyncedMana = -1;
    //Mana should be synced on the first tick.
    private boolean needsSync = true;

    public NebulaManaManager(LivingEntity entity) {
        this.entity = entity;
    }

    public void tick() {
        this.regenMana();
        if (mana > capacity()) {
            setMana(capacity());
        }
        if (this.needsSync) {
            this.sendSync();
            this.needsSync = false;
        }
    }

    @Override
    public float capacity() {
        return entity.getMaxHealth();
    }

    public void regenMana() {
        try(Transaction tx = Transaction.openOuter()) {
            insertMana(this.getManaRegenRate(), tx);
            tx.commit();
        }
    }

    private float getManaRegenRate() {
        return this.entity.isMobOrPlayer() ? 0.005f : 0;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.setMana(mana, this.needsSyncing());
    }

    public void setMana(float mana, boolean syncToClient) {
        this.mana = Math.max(Math.min(mana, this.capacity()), 0);
        if (syncToClient) this.querySync();
    }

    @Override
    public float insertMana(float amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        float insertion = Math.min(amount, capacity());

        var shouldInsert = ManaInsertionCallback.BEFORE.invoker().canInsertMana(InsertionContext.create(this.entity, this.mana, insertion, amount))
                // implicit NaN check (as NaN > x = false)
                && insertion > 0;

        if (shouldInsert) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
        }

        ManaInsertionCallback.AFTER.invoker().onManaInsertion(InsertionContext.create(this.entity, this.mana, insertion, amount));

        return insertion;
    }

    @Override
    public float extractMana(float amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        float extraction = Math.min(amount, capacity());

        var shouldExtract = ManaExtractionCallback.BEFORE.invoker().canExtractMana(ExtractionContext.create(this.entity, this.mana, extraction, amount))
                // implicit NaN check (as NaN > x = false)
                && extraction > 0;

        if (shouldExtract) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
        }

        ManaExtractionCallback.AFTER.invoker().onManaExtraction(ExtractionContext.create(this.entity, this.mana, extraction, amount));

        return extraction;
    }

    @Override
    protected void onFinalCommit() {
        this.querySync();
    }

    public void querySync() {
        this.needsSync = true;
    }

    public boolean sendSync() {
        if (this.entity instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            float syncMana = this.getMana();
            if (syncMana == this.lastSyncedMana) return true;
            this.lastSyncedMana = syncMana;
            ServerPlayNetworking.send(serverPlayerEntity, new SyncManaPayload(syncMana));
            return true;
        }
        return false;
    }

    @SuppressWarnings("resource")
    public static void receive(SyncManaPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> context.player().getManaManager().setMana(payload.mana()));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.putFloat(MANA_NBT_KEY, this.getMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.setMana(nebulaNbt.getFloat(MANA_NBT_KEY), false);
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
        return entity instanceof ServerPlayerEntity;

    }
}
