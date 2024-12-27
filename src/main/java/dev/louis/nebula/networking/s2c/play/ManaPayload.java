package dev.louis.nebula.networking.s2c.play;

import dev.louis.nebula.Nebula;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record ManaPayload(int entityId, long mana, long capacity) implements CustomPayload {
    public static final Id<ManaPayload> ID = new CustomPayload.Id<>(Identifier.of(Nebula.MOD_ID, "mana"));
    public static final PacketCodec<PacketByteBuf, ManaPayload> CODEC = PacketCodec.of(ManaPayload::write, ManaPayload::read);

    public static ManaPayload read(PacketByteBuf buf) {
        return new ManaPayload(buf.readVarInt(), buf.readLong() , buf.readLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeLong(mana);
        buf.writeLong(capacity);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
