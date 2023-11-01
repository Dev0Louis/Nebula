package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;

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
}
