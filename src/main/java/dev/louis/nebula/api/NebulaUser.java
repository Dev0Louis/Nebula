package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.world.World;

public interface NebulaUser {
    ManaManager getManaManager();
    ManaManager setManaManager(ManaManager manaManager);
    SpellManager getSpellManager();
    SpellManager setSpellManager(SpellManager spellManager);

    double getX();
    double getY();
    double getZ();
    World getWorld();
}
