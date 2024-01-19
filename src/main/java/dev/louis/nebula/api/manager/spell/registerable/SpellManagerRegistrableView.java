package dev.louis.nebula.api.manager.spell.registerable;

import dev.louis.nebula.api.manager.spell.SpellManager;

public interface SpellManagerRegistrableView {
        void registerSpellManagerFactory(SpellManager.Factory<?> manaManagerFactory);
}