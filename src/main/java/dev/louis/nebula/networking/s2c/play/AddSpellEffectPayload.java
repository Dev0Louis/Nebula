package dev.louis.nebula.networking.s2c.play;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.SpellEffectType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AddSpellEffectPayload(SpellEffectType<?> spellEffectType) {
    public static final CustomPayload.Id<AddSpellEffectPayload> ID = new CustomPayload.Id<>(Identifier.of(Nebula.MOD_ID, "add_spell_effect"));
    public static final PacketCodec<PacketByteBuf, AddSpellEffectPayload> CODEC = PacketCodec.of(AddSpellEffectPayload::write, AddSpellEffectPayload::read);

}
