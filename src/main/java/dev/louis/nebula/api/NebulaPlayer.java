package dev.louis.nebula.api;

import dev.louis.nebula.api.manager.mana.ManaManager;
import dev.louis.nebula.api.manager.spell.SpellManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

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


    static ManaManager getManaManager(ServerPlayerEntity serverPlayer) {
        return serverPlayer.getManaManager();
    }

    static ManaManager setManaManager(ServerPlayerEntity serverPlayer, ManaManager manaManager) {
        return serverPlayer.setManaManager(manaManager);
    }

    static SpellManager getSpellManager(ServerPlayerEntity serverPlayer) {
        return serverPlayer.getSpellManager();
    }

    static SpellManager setSpellManager(ServerPlayerEntity serverPlayer, SpellManager spellManager) {
        return serverPlayer.setSpellManager(spellManager);
    }

    /**
     * This creates the ManaManager and the SpellManager if they are not already created.
     * This should not be called manually, unless you know what you are doing.
     */
    default void createManagersIfNecessary() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    };
}
