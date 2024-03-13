package dev.louis.nebula.api.manager.spell.entrypoint;

import dev.louis.nebula.api.manager.spell.registerable.SpellManagerRegistrableView;

/**
 * The entrypoint for registering a custom spell manager.
 */
public interface RegisterSpellManagerEntrypoint {
    void registerSpellManager(SpellManagerRegistrableView spellManagerRegistrableView);

    default boolean shouldRegister() {
        return true;
    };
}
