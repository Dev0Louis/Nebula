package dev.louis.nebula.api;

import dev.louis.nebula.mana.manager.ManaManager;
import dev.louis.nebula.spell.manager.SpellManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface InjectedNebulaUser extends NebulaUser {
    default ManaManager getManaManager() {
        return null;
    };
    default ManaManager setManaManager(ManaManager manaManager) {
        return null;
    };
    default SpellManager getSpellManager() {
        return null;
    };
    default SpellManager setSpellManager(SpellManager spellManager) {
        return null;
    };
}
