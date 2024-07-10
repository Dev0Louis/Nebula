package dev.louis.nebula;

import dev.louis.nebula.manager.mana.NebulaManaManager;
import dev.louis.nebula.manager.spell.NebulaSpellManager;
import dev.louis.nebula.networking.s2c.SyncManaPayload;
import dev.louis.nebula.networking.s2c.UpdateSpellCastabilityPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    public void registerPacketReceivers() {

        ClientPlayNetworking.registerGlobalReceiver(UpdateSpellCastabilityPayload.ID, NebulaSpellManager::receiveSync);

        ClientPlayNetworking.registerGlobalReceiver(SyncManaPayload.ID, NebulaManaManager::receiveSync);
    }
}
