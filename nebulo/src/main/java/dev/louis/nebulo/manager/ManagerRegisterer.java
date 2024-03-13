package dev.louis.nebulo.manager;

import dev.louis.nebula.api.manager.mana.entrypoint.RegisterManaManagerEntrypoint;
import dev.louis.nebula.api.manager.mana.registerable.ManaManagerRegistrableView;
import dev.louis.nebula.api.manager.spell.entrypoint.RegisterSpellManagerEntrypoint;
import dev.louis.nebula.api.manager.spell.registerable.SpellManagerRegistrableView;
import dev.louis.nebulo.manager.mana.NebuloManaManager;
import dev.louis.nebulo.manager.spell.NebuloSpellManager;

public class ManagerRegisterer implements RegisterManaManagerEntrypoint, RegisterSpellManagerEntrypoint {
    @Override
    public void registerManaManager(ManaManagerRegistrableView manaManagerRegistrableView) {
        manaManagerRegistrableView.registerManaManager(NebuloManaManager::new);
    }

    @Override
    public void registerSpellManager(SpellManagerRegistrableView spellManagerRegistrableView) {
        spellManagerRegistrableView.registerSpellManager(NebuloSpellManager::new);
    }

    @Override
    public boolean shouldRegister() {
        return true;
    }
}
