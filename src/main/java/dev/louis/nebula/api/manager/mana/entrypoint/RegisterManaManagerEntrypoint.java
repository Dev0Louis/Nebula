package dev.louis.nebula.api.manager.mana.entrypoint;

import dev.louis.nebula.api.manager.mana.registerable.ManaManagerRegistrableView;

/**
 * The entrypoint for registering a custom mana manager.
 */
public interface RegisterManaManagerEntrypoint {
    void registerSpell(ManaManagerRegistrableView manaManagerRegistrableView);

    default boolean shouldRegister() {
        return true;
    };
}
