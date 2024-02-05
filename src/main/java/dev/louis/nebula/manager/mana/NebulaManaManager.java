package dev.louis.nebula.manager.mana;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.manager.mana.ManaManager;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.mixin.ClientPlayerEntityAccessor;
import dev.louis.nebula.networking.SyncManaS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Extending this class is okay, but be aware this implementation!
 */
public class NebulaManaManager implements ManaManager {
    protected static final String MANA_NBT_KEY = "Mana";
    protected PlayerEntity player;
    protected int mana = 0;
    protected int lastSyncedMana = -1;
    private boolean dirty;

    public NebulaManaManager(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        if(this.dirty) {
            this.sendSync();
            this.dirty = false;
        }
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public void setMana(int mana) {
        this.setMana(mana, this.isServer());
    }

    public void setMana(int mana, boolean syncToClient) {
        this.mana = Math.max(Math.min(mana, this.getMaxMana()), 0);
        if(syncToClient) this.markDirty();
    }

    @Override
    public void addMana(int mana) {
        this.setMana(this.getMana() + mana);
    }

    @Override
    public void drainMana(int mana) {
        this.setMana(this.getMana() - mana);
    }

    @Override
    public void drainMana(SpellType<?> spellType) {
        this.drainMana(spellType.getManaCost());
    }

    @Override
    public int getMaxMana() {
        return 20;
    }

    @Override
    public boolean hasEnoughMana(int mana) {
        return this.getMana() >= mana;
    }

    @Override
    public boolean isCastable(SpellType<?> spellType) {
        return this.hasEnoughMana(spellType.getManaCost());
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean sendSync() {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            int syncMana = this.getMana();
            if (syncMana == this.lastSyncedMana) return true;
            this.lastSyncedMana = syncMana;
            ServerPlayNetworking.send(serverPlayerEntity, new SyncManaS2CPacket(syncMana));
            return true;
        }
        return false;
    }

    public static boolean receiveSync(SyncManaS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ((ClientPlayerEntityAccessor)player).getClient().executeSync(() -> player.getManaManager().setMana(packet.mana()));
        return true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.putInt(MANA_NBT_KEY, this.getMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.setMana(nebulaNbt.getInt(MANA_NBT_KEY), false);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        setMana(0);
    }

    @Override
    public ManaManager setPlayer(PlayerEntity player) {
        this.player = player;
        return this;
    }

    public boolean isServer() {
        return !player.getWorld().isClient();
    }
}
