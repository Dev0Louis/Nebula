package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.player.PlayerEntity;

/**
 * This interface will be injected into {@link PlayerEntity}.
 *
 */
public interface NebulaPlayer {
    default ManaManager getManaManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    default ManaManager setManaManager(ManaManager manaManager) {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    default SpellManager getSpellManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    default SpellManager setSpellManager(SpellManager spellManager) {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    /**
     * This creates the ManaManager and the SpellManager if they are not already created.
     * This should not be called manually, unless you know what you are doing.
     */
    default void createManagersIfNecessary() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    };
}
