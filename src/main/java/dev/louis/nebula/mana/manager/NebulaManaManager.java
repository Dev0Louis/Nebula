package dev.louis.nebula.mana.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class NebulaManaManager implements ManaManager {
    protected static final String MANA_NBT_KEY = "Mana";
    protected PlayerEntity player;
    protected int mana = 0;
    protected int lastSyncedMana = -1;

    public NebulaManaManager(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
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
        if(syncToClient) this.sendSync();
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
    public boolean hasEnoughMana(SpellType<?> spellType) {
        return this.hasEnoughMana(spellType.getManaCost());
    }

    @Override
    public boolean sendSync() {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            int syncMana = this.getMana();
            if (syncMana == this.lastSyncedMana) return true;
            this.lastSyncedMana = syncMana;
            ServerPlayNetworking.send(
                    serverPlayerEntity,
                    new SynchronizeManaAmountS2CPacket(syncMana)
            );
            return true;
        }
        return false;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(this.isServer()) {
            Nebula.LOGGER.error("Called receiveSync on server side!");
            return false;
        }
        var packet = new SynchronizeManaAmountS2CPacket(buf);
        this.player.getManaManager().setMana(packet.mana());
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
