package dev.louis.nebula.api.manager.registerable;

import dev.louis.nebula.spell.manager.SpellManager;

public interface SpellManagerRegistrableView {
        void registerSpellManagerFactory(SpellManager.Factory<?> manaManagerFactory);
}