package dev.louis.nebula.api.manager.entrypoint;

import dev.louis.nebula.api.manager.registerable.ManaManagerRegistrableView;

/**
 * The entrypoint for registering a custom mana manager.
 */
public interface RegisterManaManagerEntrypoint {
    void registerSpell(ManaManagerRegistrableView manaManagerRegistrableView);

    default boolean shouldRegister() {
        return true;
    };
}
