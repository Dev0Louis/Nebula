package dev.louis.nebula.api.manager.entrypoint;

import dev.louis.nebula.api.manager.registerable.SpellManagerRegistrableView;

public interface RegisterSpellManagerEntrypoint {
    void registerSpell(SpellManagerRegistrableView spellManagerRegistrableView);

    default boolean shouldRegister() {
        return true;
    };
}