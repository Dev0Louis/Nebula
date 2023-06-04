package dev.louis.nebula.mana.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface ManaManager {
    void tick();
    void setMana(int mana);
    int getMana();
    void addMana(int mana);
    void drainMana(int mana);
    int getPlayerMaxMana();
    void setPlayerMaxMana(int mana);
    void sync();
    void writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);
    void copyFrom(PlayerEntity oldPlayer, boolean alive);
    @FunctionalInterface
    interface Factory<T extends ManaManager> {
        T createPlayerManaManager(PlayerEntity player);
    }
}
