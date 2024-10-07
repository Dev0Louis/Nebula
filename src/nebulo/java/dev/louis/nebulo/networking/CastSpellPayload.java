package dev.louis.nebulo.networking;

import dev.louis.nebulo.Nebulo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CastSpellPayload(Identifier spellId) implements CustomPayload {
    public static final Id<CastSpellPayload> ID = new Id<>(Identifier.of(Nebulo.MOD_ID, "spell_cast"));
    public static final PacketCodec<PacketByteBuf, CastSpellPayload> CODEC = PacketCodec.of(CastSpellPayload::write, CastSpellPayload::read);

    public static CastSpellPayload read(PacketByteBuf buf) {
        return new CastSpellPayload(buf.readIdentifier());
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(spellId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
