package dev.louis.nebula.mana;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaManager;
import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class NebulaManaManager extends SnapshotParticipant<Float> implements ManaPool, ManaManager  {
    public static final String MANA_NBT_KEY = "Mana";
    public static final float DEFAULT_CAPACITY = 20;
    protected LivingEntity entity;
    protected float capacity;
    protected float mana;
    protected float lastSyncedMana = -1;
    //Mana should be synced on the first tick.
    private boolean needsSync = true;

    public NebulaManaManager(LivingEntity entity) {
        this(entity, DEFAULT_CAPACITY);
    }

    public NebulaManaManager(LivingEntity entity, float capacity) {
        this.entity = entity;
        this.mana = 0;
        this.capacity = capacity;
    }

    public void tick() {
        if (this.needsSync) {
            this.sendSync();
            this.needsSync = false;
        }
    }

    @Override
    public float capacity() {
        return capacity;
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
        float insertion = Math.min(amount, capacity - mana);

        if (insertion > 0) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
            return insertion;
        }

        return 0;
    }

    @Override
    public float extractMana(float amount, TransactionContext context) {
        float extraction = Math.min(amount, capacity - mana);

        if (extraction > 0) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
            return extraction;
        }

        return 0;
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

    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.putFloat(MANA_NBT_KEY, this.getMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

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
