package dev.louis.nebula;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class NebulaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    private void registerPacketReceivers() {
        //Register the Spell Sync Packet.
        registerReceiver(UpdateSpellCastabilityS2CPacket.getID(), ((client, handler, buf1, responseSender) -> {
            executeSyncWithBuf(client, buf1, (buf -> NebulaPlayer.access(client.player).getSpellManager().receiveSync(client, handler, buf, responseSender)));
        }));

        //Register the ManaAmount Packet.
        registerReceiver(SynchronizeManaAmountS2CPacket.getId(), ((client, handler, buf1, responseSender) -> {
            executeSyncWithBuf(client, buf1, (buf -> NebulaPlayer.access(client.player).getManaManager().receiveSync(client, handler, buf, responseSender)));
        }));
    }

    public static void executeSyncWithBuf(MinecraftClient client, PacketByteBuf buf, Consumer<PacketByteBuf> consumer) {
        buf.retain();
        client.executeSync(() -> {
            consumer.accept(buf);
            buf.release();
        });
    }

    private void registerReceiver(Identifier id, ClientPlayNetworking.PlayChannelHandler playChannelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(id, playChannelHandler);
    }
}
