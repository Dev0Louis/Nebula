package dev.louis.nebula.api.manager.mana.registerable;

import dev.louis.nebula.api.manager.mana.ManaManager;

public interface ManaManagerRegistrableView {
        void registerManaManager(ManaManager.Factory<?> manaManagerFactory);
}