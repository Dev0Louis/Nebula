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
    public void registerSpell(ManaManagerRegistrableView manaManagerRegistrableView) {
        manaManagerRegistrableView.registerManaManager(
                NebuloManaManager::new,
                SyncManaS2CPacket.ID,
                (packetId) -> ClientPlayNetworking.registerGlobalReceiver(packetId, NebulaManaManager::receiveSync)
        );
    }

    @Override
    public void registerSpell(SpellManagerRegistrableView spellManagerRegistrableView) {
        spellManagerRegistrableView.registerSpellManager(
                NebuloSpellManager::new,
                UpdateSpellCastabilityS2CPacket.ID,
                (packetId) -> ClientPlayNetworking.registerGlobalReceiver(packetId, NebulaSpellManager::receiveSync)
        );
    }

    @Override
    public boolean shouldRegister() {
        return true;
    }
}
