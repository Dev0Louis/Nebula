package dev.louis.nebula.mana.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
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
    public NebulaManaManager(PlayerEntity player) {
        this.player = player;
    }
    private int mana = 0;
    //TODO: Implement this:
    //private int lastSyncedMana = getMana();


    @Override
    public void tick() {
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana, boolean syncToClient) {
        this.mana = Math.max(Math.min(mana, getMaxMana()), 0);
        if(syncToClient) {
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

    public int getMaxMana() {
        return 20;
    }

    @Override
    public boolean sendSync() {
        if(this.player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.networkHandler != null) {
            ServerPlayNetworking.send(
                    serverPlayerEntity,
                    new SynchronizeManaAmountS2CPacket(player.getManaManager().getMana())
            );
            return true;
        }
        return false;
    }

    @Override
    public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var packet = new SynchronizeManaAmountS2CPacket(buf);
        player.getManaManager().setMana(packet.mana());
        return true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);

        nebulaNbt.putInt("Mana", this.getMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.setMana(nebulaNbt.getInt("Mana"));
    }

    public void copyFrom(PlayerEntity oldPlayer, boolean alive) {
        if(alive) {
            ManaManager oldManaManager = oldPlayer.getManaManager();
            this.setMana(oldManaManager.getMana());
        }
    }

    public NebulaManaManager setPlayer(PlayerEntity player) {
        this.player = player;
        return this;
    }
}
