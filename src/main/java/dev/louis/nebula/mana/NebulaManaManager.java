package dev.louis.nebula.mana;

import dev.louis.nebula.InternalNebulaPlayer;
import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaContainer;
import dev.louis.nebula.api.mana.ManaHolder;
import dev.louis.nebula.api.mana.ManaManager;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Extending this class is okay, but be aware this implementation!
 */
public class NebulaManaManager implements ManaManager  {
    protected static final String MANA_NBT_KEY = "Mana";
    protected PlayerEntity player;
    protected ManaHolder manaHolder = new ManaContainer(0, 20);
    protected int lastSyncedMana = -1;
    //Mana should be synced on the first tick.
    private boolean needsSync = true;

    public NebulaManaManager(PlayerEntity player) {
        this.player = player;
    }

    public void tick() {
        if (this.needsSync) {
            this.sendSync();
            this.needsSync = false;
        }
    }

    public int getMana() {
        return manaHolder.mana();
    }

    public void setMana(int mana) {
        this.setMana(mana, this.isServer());
    }

    public void setMana(int mana, boolean syncToClient) {
        manaHolder.setMana(Math.max(Math.min(mana, this.getMaxMana()), 0));
        if (syncToClient) this.querySync();
    }

    public void addMana(int mana, TransactionContext context) {
        var inserted = manaHolder.insert(mana, context);
        if (inserted > 0) syncIfCommited(context);
    }

    public void drainMana(int mana, TransactionContext context) {
        var extracted = manaHolder.extract(mana, context);
        if (extracted > 0) syncIfCommited(context);
    }

    private void syncIfCommited(TransactionContext context) {
        context.addCloseCallback((transaction, result) -> {
            if (result == TransactionContext.Result.COMMITTED) {
                this.querySync();
            }
        });
    }

    public int getMaxMana() {
        //TODO: Rename method.
        return manaHolder.capacity();
    }

    public boolean hasEnoughMana(int mana) {
        return this.getMana() >= mana;
    }

    public boolean hasEnoughMana(SpellType<?> spellType) {
        return this.hasEnoughMana(spellType.getManaCost());
    }

    public void querySync() {
        this.needsSync = true;
    }

    public boolean sendSync() {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            System.out.println("BBBBBB");
            int syncMana = this.getMana();
            System.out.println("Syncing Mana: " + syncMana);
            if (syncMana == this.lastSyncedMana) return true;
            this.lastSyncedMana = syncMana;
            ServerPlayNetworking.send(serverPlayerEntity, new SyncManaPayload(syncMana));
            System.out.println("SEND PACKET");
            return true;
        }
        return false;
    }

    public static void receive(SyncManaPayload payload, ClientPlayNetworking.Context context) {
        System.out.println("AAAAAAAAAAAAA");
        context.client().executeSync(() -> InternalNebulaPlayer.getManaManager(context.player()).setMana(payload.mana()));
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.putInt(MANA_NBT_KEY, this.getMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.setMana(nebulaNbt.getInt(MANA_NBT_KEY), false);
    }

    public void copyFrom(NebulaManaManager manaManager) {
        this.setMana(manaManager.getMana());
    }

    public boolean isServer() {
        return !player.getWorld().isClient();
    }
}
