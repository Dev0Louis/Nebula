package dev.louis.nebula.mana.manager;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
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
    private PlayerEntity player;
    public NebulaManaManager(PlayerEntity player) {
        this.player = player;
    }
    private int mana = 0;
    private int lastSyncedMana = -1;


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
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.networkHandler != null) {
                int mana = this.player.getManaManager().getMana();
                if (mana == this.lastSyncedMana) return true;
                ServerPlayNetworking.send(
                        serverPlayerEntity,
                        new SynchronizeManaAmountS2CPacket(mana)
                );
                this.lastSyncedMana = mana;
                return true;
            }
            Nebula.LOGGER.error("sendSync was called to early for " + serverPlayerEntity.getEntityName());
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

    @Override
    public void onDeath(DamageSource damageSource) {
        //Nothing here :)
    }

    public NebulaManaManager setPlayer(PlayerEntity player) {
        this.player = player;
        return this;
    }
}
