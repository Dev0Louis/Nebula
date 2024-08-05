package dev.louis.nebula;

import dev.louis.nebula.mana.NebulaManaManager;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    public void registerPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SyncManaPayload.ID, NebulaManaManager::receive);
    }
}
