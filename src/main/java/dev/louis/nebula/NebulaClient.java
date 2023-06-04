package dev.louis.nebula;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.networking.SynchronizeSpellsS2CPacket;
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
        registerReceiver(SynchronizeSpellsS2CPacket.getID(), ((client, handler, buf, responseSender) -> {
            NebulaPlayer.access(client.player).getSpellManager().receiveSync(client, handler, buf, responseSender);
        }));

        //Register the ManaAmount Packet.
        registerReceiver(SynchronizeManaAmountS2CPacket.getId(), ((client, handler, buf, responseSender) -> {
            NebulaPlayer.access(client.player).getManaManager().receiveSync(client, handler, buf, responseSender);
        }));
    }

    private void registerReceiver(Identifier id, ClientPlayNetworking.PlayChannelHandler playChannelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(id, playChannelHandler);
    }
}
