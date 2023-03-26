package dev.louis.nebula.manamanager;

import dev.louis.nebula.manamanager.player.PlayerManaManager;
import net.minecraft.entity.player.PlayerEntity;

public interface ManaManager<T extends PlayerManaManager> {
    int getMaxMana();
    T createPlayerManaManager(PlayerEntity player);
    void setUp();
}
