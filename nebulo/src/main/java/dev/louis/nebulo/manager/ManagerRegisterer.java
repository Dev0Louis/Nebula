package dev.louis.nebulo.manager;

import dev.louis.nebula.api.manager.mana.entrypoint.RegisterManaManagerEntrypoint;
import dev.louis.nebula.api.manager.mana.registerable.ManaManagerRegistrableView;
import dev.louis.nebula.api.manager.spell.entrypoint.RegisterSpellManagerEntrypoint;
import dev.louis.nebula.api.manager.spell.registerable.SpellManagerRegistrableView;
import dev.louis.nebula.manager.mana.NebulaManaManager;
import dev.louis.nebula.manager.spell.NebulaSpellManager;
import dev.louis.nebula.networking.SyncManaS2CPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import dev.louis.nebulo.manager.mana.NebuloManaManager;
import dev.louis.nebulo.manager.spell.NebuloSpellManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ManagerRegisterer implements RegisterManaManagerEntrypoint, RegisterSpellManagerEntrypoint {
    @Override
    public void registerManaManager(ManaManagerRegistrableView manaManagerRegistrableView) {
        manaManagerRegistrableView.registerManaManager(NebuloManaManager::new);
    }

    @Override
    public void registerManaPacketReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SyncManaS2CPacket.TYPE, NebulaManaManager::receiveSync);
    }

    @Override
    public void registerSpellManager(SpellManagerRegistrableView spellManagerRegistrableView) {
        spellManagerRegistrableView.registerSpellManager(NebuloSpellManager::new);
    }

    @Override
    public void registerSpellPacketReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateSpellCastabilityS2CPacket.TYPE, NebulaSpellManager::receiveSync);
    }

    @Override
    public boolean shouldRegister() {
        return true;
    }
}
