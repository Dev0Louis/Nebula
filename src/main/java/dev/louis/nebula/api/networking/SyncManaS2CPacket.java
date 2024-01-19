package dev.louis.nebula.api.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.mixin.ClientPlayerEntityAccessor;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SyncManaS2CPacket(int mana) implements FabricPacket {
    private static final Identifier ID = new Identifier(Nebula.MOD_ID, "synchronizemana");
    public static final PacketType<SyncManaS2CPacket> TYPE = PacketType.create(ID, SyncManaS2CPacket::read);

    public static SyncManaS2CPacket read(PacketByteBuf buf) {
        return new SyncManaS2CPacket(buf.readVarInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(mana);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void receive(SyncManaS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        //Client isn't accessible here without a mixin.
        MinecraftClient client = ((ClientPlayerEntityAccessor) player).getClient();
        client.executeSync(() -> player.getManaManager().receiveSync(packet));
    }
}
