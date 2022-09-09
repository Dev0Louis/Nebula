package org.d1p4k.nebula.packet.s2c;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;

public class ManaAmountS2CPacket {

    public static Identifier packetId = new Identifier("nebula", "manaamount");

    public static void send(ServerPlayerEntity player, byte mana) {
        if(player.networkHandler != null) {
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeByte(mana);

            ServerPlayNetworking.send(player, packetId, packetByteBuf);
        }
    }

    public static void send(ServerPlayerEntity player) {
        if(player.networkHandler != null) {
            send(player, (byte) ((NebulaPlayer) player).getMana());
        }else {
            throw new IllegalStateException("The Mana was being send without networkHandler being ready!");
        }
    }
}
