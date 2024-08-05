package dev.louis.nebula.networking.s2c.play;

import dev.louis.nebula.Nebula;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncManaPayload(int mana) implements CustomPayload {
    public static final Id<SyncManaPayload> ID = new CustomPayload.Id<>(Identifier.of(Nebula.MOD_ID, "synchronize_mana"));
    public static final PacketCodec<PacketByteBuf, SyncManaPayload> CODEC = PacketCodec.of(SyncManaPayload::write, SyncManaPayload::read);

    public static SyncManaPayload read(PacketByteBuf buf) {
        return new SyncManaPayload(buf.readVarInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(mana);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
