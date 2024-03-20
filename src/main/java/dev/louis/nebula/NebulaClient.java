package dev.louis.nebula;

import dev.louis.nebula.manager.mana.NebulaManaManager;
import dev.louis.nebula.manager.spell.NebulaSpellManager;
import dev.louis.nebula.networking.SyncManaS2CPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    public void registerPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateSpellCastabilityS2CPacket.TYPE, NebulaSpellManager::receiveSync);
        ClientPlayNetworking.registerGlobalReceiver(SyncManaS2CPacket.TYPE, NebulaManaManager::receiveSync);
    }
}
