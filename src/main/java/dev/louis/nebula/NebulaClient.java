package dev.louis.nebula;

import dev.louis.nebula.api.networking.SyncManaS2CPacket;
import dev.louis.nebula.api.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    private void registerPacketReceivers() {
        //Register the Spell Sync Packet.
        ClientPlayNetworking.registerGlobalReceiver(UpdateSpellCastabilityS2CPacket.TYPE, UpdateSpellCastabilityS2CPacket::receive);

        //Register the ManaAmount Packet.
        ClientPlayNetworking.registerGlobalReceiver(SyncManaS2CPacket.TYPE, SyncManaS2CPacket::receive);

    }
}
