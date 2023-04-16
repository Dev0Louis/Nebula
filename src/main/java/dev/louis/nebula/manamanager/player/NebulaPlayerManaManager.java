package dev.louis.nebula.manamanager.player;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.NebulaManager;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class NebulaPlayerManaManager implements PlayerManaManager {
    private PlayerEntity player;
    public NebulaPlayerManaManager(PlayerEntity p) {
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
            sync();
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
        this.maxmana = NebulaManager.INSTANCE.getManaManager().getMaxMana();
    }

    @Override
    public void sync() {
        if(this.player.getWorld().isClient() || ((ServerPlayerEntity)player).networkHandler == null)return;
        var buf = PacketByteBufs.create();
        new SynchronizeManaAmountS2CPacket(((NebulaPlayer)player).getMana()).write(buf);

        ServerPlayNetworking.send(
                (ServerPlayerEntity) this.player,
                SynchronizeManaAmountS2CPacket.getId(),
                buf
        );
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
            PlayerManaManager oldManaManager = NebulaPlayer.access(oldPlayer).getPlayerManaManager();
            this.setMana(oldManaManager.getMana());
            this.setPlayerMaxMana(oldManaManager.getPlayerMaxMana());
        }
    }

    public NebulaPlayerManaManager setPlayer(PlayerEntity p) {
        this.player = p;
        return this;
    }
}
