package dev.louis.nebula;

import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    private void registerPacketReceivers() {
        //Register the Spell Sync Packet.
        registerReceiver(UpdateSpellCastabilityS2CPacket.ID,UpdateSpellCastabilityS2CPacket::receive);

        //Register the ManaAmount Packet.
        registerReceiver(SynchronizeManaAmountS2CPacket.ID, SynchronizeManaAmountS2CPacket::receive);
    }

    private void registerReceiver(Identifier id, ClientPlayNetworking.PlayChannelHandler playChannelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(id, playChannelHandler);
    }
}
