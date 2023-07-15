package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface InjectedNebulaUser extends NebulaUser {
    default ManaManager getManaManager() {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }
    default ManaManager setManaManager(ManaManager manaManager) {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }
    default SpellManager getSpellManager() {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }
    default SpellManager setSpellManager(SpellManager spellManager) {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }

    default double getX() {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }
    default double getY() {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }
    default double getZ() {
        throw new UnsupportedOperationException("InjectedInterface Method was not overridden!");
    }
}
