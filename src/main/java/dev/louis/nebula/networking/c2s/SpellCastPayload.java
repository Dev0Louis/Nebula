package dev.louis.nebula.networking.c2s;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SpellCastPayload implements CustomPayload {
    public static final Id<SpellCastPayload> ID = new CustomPayload.Id<>(Identifier.of(Nebula.MOD_ID, "spellcast"));
    public static final PacketCodec<RegistryByteBuf, SpellCastPayload> CODEC = PacketCodec.of(
            SpellCastPayload::write,
            SpellCastPayload::read
    );
    public final SpellType<?> spellType;

    public SpellCastPayload(SpellType<?> spellType) {
        this.spellType = spellType;
    }

    public static SpellCastPayload read(RegistryByteBuf buf) {
        SpellType<?> spellType = PacketCodecs.registryValue(SpellType.REGISTRY_KEY).decode(buf);

        if (spellType == null) throw new IllegalStateException("Spell type not found in registry");
        return new SpellCastPayload(spellType);
    }

    public void write(RegistryByteBuf buf) {
        PacketCodecs.registryValue(SpellType.REGISTRY_KEY).encode(buf, this.spellType);
    }

    public static void receive(SpellCastPayload packet, ServerPlayNetworking.Context context) {
        context.player().server.executeSync(() -> context.player().getSpellManager().cast(packet.spellType));
    }

    @Override
    public String toString() {
        return "SpellCastC2SPacket[" +
                "spellType=" + spellType + ']';
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
