package dev.louis.nebula.manamanager;

import dev.louis.nebula.manamanager.player.NebulaPlayerManaManager;
import net.minecraft.entity.player.PlayerEntity;

public class NebulaManaManager implements ManaManager<NebulaPlayerManaManager> {
    private static final int MAX_MANA = 20;
    @Override
    public int getMaxMana() {
        return MAX_MANA;
    }

    @Override
    public NebulaPlayerManaManager createPlayerManaManager(PlayerEntity player) {
        return new NebulaPlayerManaManager(player);
    }

    @Override
    public void setUp(){}
}
