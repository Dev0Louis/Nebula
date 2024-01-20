package dev.louis.nebula;

import net.fabricmc.api.ClientModInitializer;

public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NebulaManager.registerPacketReceivers();
    }
}
