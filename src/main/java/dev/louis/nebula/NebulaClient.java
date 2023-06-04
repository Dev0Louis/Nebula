package dev.louis.nebula;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.networking.SynchronizeSpellsS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NebulaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    private void registerPacketReceivers() {
        //Register the Spell Sync Packet.
        ClientPlayNetworking.registerGlobalReceiver(SynchronizeSpellsS2CPacket.getID(), (client, handler, buf, responseSender) -> {
            SynchronizeSpellsS2CPacket packet = SynchronizeSpellsS2CPacket.read(buf);
            client.executeSync(() -> NebulaPlayer.access(client.player).getSpellManager().updateCastableSpell(packet.spells()));
        });
        //Register the ManaAmount Packet.
        ClientPlayConnectionEvents.INIT.register(((handler, client) -> ClientPlayNetworking.registerReceiver(SynchronizeManaAmountS2CPacket.PACKET_TYPE, SynchronizeManaAmountS2CPacket::receive)));
    }
}
