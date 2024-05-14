package dev.louis.nebula;

import dev.louis.nebula.manager.mana.NebulaManaManager;
import dev.louis.nebula.manager.spell.NebulaSpellManager;
import dev.louis.nebula.networking.s2c.SyncManaPayload;
import dev.louis.nebula.networking.s2c.UpdateSpellCastabilityPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    public void registerPacketReceivers() {
        PayloadTypeRegistry.playS2C().register(UpdateSpellCastabilityPayload.ID, UpdateSpellCastabilityPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(UpdateSpellCastabilityPayload.ID, NebulaSpellManager::receiveSync);

        PayloadTypeRegistry.playS2C().register(SyncManaPayload.ID, SyncManaPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SyncManaPayload.ID, NebulaManaManager::receiveSync);
    }
}
