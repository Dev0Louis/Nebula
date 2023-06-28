package dev.louis.nebula.mana.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class NebulaManaManager implements ManaManager {
    private PlayerEntity player;
    public NebulaManaManager(PlayerEntity p) {
        this.player = p;
    }
    private int mana = 0;
    private int maxmana = 20;


    @Override
    public void tick() {
    }

    public int getMana() {
        return mana;

    }
    public void setMana(int mana, boolean sendToPlayer) {
        this.mana = Math.max(Math.min(mana, maxmana), 0);
        if(sendToPlayer) {
            sendSync();
        }
    }
    public void setMana(int mana) {
        setMana(mana, true);
    }

    public void addMana(int mana) {
        setMana(getMana() + mana);
    }

    public void drainMana(int mana) {
        setMana(getMana() - mana);
    }

    public int getPlayerMaxMana() {
        return maxmana;
    }

    public void setPlayerMaxMana(int max) {
        if(max > 0)this.maxmana = max;
    }

    @Override
    public boolean sendSync() {
        if(this.player.getWorld().isClient() || ((ServerPlayerEntity)player).networkHandler == null)return false;
        var buf = PacketByteBufs.create();
        new SynchronizeManaAmountS2CPacket((NebulaPlayer.access(player)).getManaManager().getMana()).write(buf);

        ServerPlayNetworking.send(
                (ServerPlayerEntity) this.player,
                SynchronizeManaAmountS2CPacket.getId(),
                buf
        );
        return true;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var packet = new SynchronizeManaAmountS2CPacket(buf);
        NebulaPlayer.access(player).getManaManager().setMana(packet.mana());
        return true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);

        nebulaNbt.putInt("Mana", this.getMana());
        nebulaNbt.putInt("MaxMana", this.getPlayerMaxMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.setMana(nebulaNbt.getInt("Mana"));
        this.setPlayerMaxMana(nebulaNbt.getInt("MaxMana"));
    }

    public void copyFrom(PlayerEntity oldPlayer, boolean alive) {
        if(alive) {
            ManaManager oldManaManager = NebulaPlayer.access(oldPlayer).getManaManager();
            this.setMana(oldManaManager.getMana());
            this.setPlayerMaxMana(oldManaManager.getPlayerMaxMana());
        }
    }

    public NebulaManaManager setPlayer(PlayerEntity p) {
        this.player = p;
        return this;
    }
}
