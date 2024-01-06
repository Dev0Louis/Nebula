package dev.louis.nebula.api.manager.entrypoint;

import dev.louis.nebula.api.manager.registerable.SpellManagerRegistrableView;

/**
 * The entrypoint for registering a custom spell manager.
 */
public interface RegisterSpellManagerEntrypoint {
    void registerSpell(SpellManagerRegistrableView spellManagerRegistrableView);

    default boolean shouldRegister() {
        return true;
    };
}
