package dev.louis.nebula.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface NebulaPacket {
    @Environment(EnvType.CLIENT)
    default void sendToServer() {
        ClientPlayNetworking.send(this.getId(), this.write(new PacketByteBuf(Unpooled.buffer())));
    }

    default void sendToPlayer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this.getId(), this.write(new PacketByteBuf(Unpooled.buffer())));
    }

    PacketByteBuf write(PacketByteBuf buf);

    Identifier getId();
}
