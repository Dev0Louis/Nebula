package org.d1p4k.nebula.packet.s2c;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.api.NebulaPlayer;

public class KnowledgeS2CPacket {

    public static Identifier packetId = new Identifier("nebula", "knowledge");

    public static void send(ServerPlayerEntity player, Identifier spell, boolean knows) {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeIdentifier(spell);
        packetByteBuf.writeBoolean(knows);
        ServerPlayNetworking.send(player, packetId, packetByteBuf);
    }
    public static void send(ServerPlayerEntity player, Identifier spell) {
        send(player, spell, ((NebulaPlayer) player).getSpellKnowledge().getCastableSpells().contains(spell));
    }
}
