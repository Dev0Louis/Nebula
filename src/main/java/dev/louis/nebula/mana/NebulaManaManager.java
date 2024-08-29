package dev.louis.nebula.mana;

import dev.louis.nebula.InternalNebulaPlayer;
import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaContainer;
import dev.louis.nebula.api.mana.ManaHolder;
import dev.louis.nebula.api.mana.ManaManager;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class NebulaManaManager extends SnapshotParticipant<Float> implements ManaHolder, ManaManager  {
    protected static final String MANA_NBT_KEY = "Mana";
    private static final int CAPACITY = 20;
    protected PlayerEntity player;
    protected float mana = 20;
    protected float lastSyncedMana = -1;
    protected ManaHolder manaHolder = new ManaContainer(0, 20);
    //Mana should be synced on the first tick.
    private boolean needsSync = true;

    public NebulaManaManager(PlayerEntity player) {
        this.player = player;
        this.mana = 20;
    }

    public void tick() {
        if (this.needsSync) {
            this.sendSync();
            this.needsSync = false;
        }
    }

    @Override
    public int manaCapacity() {
        return CAPACITY;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.setMana(mana, this.isServer());
    }

    public void setMana(float mana, boolean syncToClient) {
        manaHolder.setMana(Math.max(Math.min(mana, this.getCapacity()), 0));
        if (syncToClient) this.querySync();
    }

    @Override
    public float insert(float amount, TransactionContext context) {
        float insertion = Math.min(amount, CAPACITY - mana);

        if (insertion > 0) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
            return insertion;
        }

        return 0;
    }

    @Override
    public float extract(float amount, TransactionContext context) {
        float extraction = Math.min(amount, CAPACITY - mana);

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

    public int getCapacity() {
        //TODO: Rename method.
        return CAPACITY;
    }

    public boolean hasEnoughMana(int mana) {
        return this.getMana() >= mana;
    }


    public void querySync() {
        this.needsSync = true;
    }

    public boolean sendSync() {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            float syncMana = this.getMana();
            if (syncMana == this.lastSyncedMana) return true;
            this.lastSyncedMana = syncMana;
            ServerPlayNetworking.send(serverPlayerEntity, new SyncManaPayload(syncMana));
            return true;
        }
        return false;
    }

    public static void receive(SyncManaPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> InternalNebulaPlayer.getManaManager(context.player()).setMana(payload.mana()));
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

    public void copyFrom(NebulaManaManager manaManager) {
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

    public boolean isServer() {
        return !player.getWorld().isClient();
    }
}
