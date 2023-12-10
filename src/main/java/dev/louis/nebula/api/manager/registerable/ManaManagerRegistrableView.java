package dev.louis.nebula.api.manager.registerable;

import dev.louis.nebula.mana.manager.ManaManager;

public interface ManaManagerRegistrableView {
        void registerManaManagerFactory(ManaManager.Factory<?> manaManagerFactory);
}