package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.MultiTickSpell;
import dev.louis.nebula.spell.manager.SpellManager;

import java.util.Collection;

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

    default Collection<MultiTickSpell> setMultiTickSpells(Collection<MultiTickSpell> multiTickSpells) {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    default Collection<MultiTickSpell> getMultiTickSpells() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }
}
