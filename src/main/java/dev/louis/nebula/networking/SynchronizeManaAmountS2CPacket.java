package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static dev.louis.nebula.Nebula.runSyncWithBuf;

public record SynchronizeManaAmountS2CPacket(int mana) implements NebulaPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "synchronizemana");

    public PacketByteBuf write(PacketByteBuf buf) {
        return buf.writeVarInt(mana);
    }

    public static SynchronizeManaAmountS2CPacket read(PacketByteBuf buf) {
        return new SynchronizeManaAmountS2CPacket(buf.readVarInt());
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        runSyncWithBuf(
                client,
                buf,
                () -> client.player.getManaManager().receiveSync(client, handler, buf, responseSender)
        );
    }

    public Identifier getId() {
        return ID;
    }
}
