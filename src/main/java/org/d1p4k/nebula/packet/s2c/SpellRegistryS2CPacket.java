package org.d1p4k.nebula.packet.s2c;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.nebula.knowledge.SpellKnowledge;

import java.util.Collection;

public class SpellRegistryS2CPacket {

    public static Identifier packetId = new Identifier("nebula", "spellregistry");

    public static void send(ServerPlayerEntity player) {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        Collection<Identifier> registeredSpells = SpellKnowledge.Registry.getRegisteredSpells();
        packetByteBuf.writeVarInt(registeredSpells.size());

        for (Identifier spell : registeredSpells) {
            packetByteBuf.writeIdentifier(spell);
        }

        ServerPlayNetworking.send(player, packetId, packetByteBuf);

    }
}
