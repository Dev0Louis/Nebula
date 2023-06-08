package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SynchronizeManaAmountS2CPacket(int mana) implements FabricPacket {
    public static final PacketType<SynchronizeManaAmountS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "synchronizemana"), SynchronizeManaAmountS2CPacket::new);

    public SynchronizeManaAmountS2CPacket(PacketByteBuf buf) {
        this(buf.readVarInt());
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(mana);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static Identifier getId() {
        return PACKET_TYPE.getId();
    }
}
