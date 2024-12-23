
package dev.louis.nebula.networking.s2c.play;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebula.api.spell.effect.SpellEffects;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record StopSpellEffectPayload(int entityId, RegistryEntry<SpellEffect> entry) implements CustomPayload {
    public static final Id<StopSpellEffectPayload> ID = new Id<>(Identifier.of(Nebula.MOD_ID, "stop_spell_effect"));
    public static final PacketCodec<RegistryByteBuf, StopSpellEffectPayload> CODEC = PacketCodec.of(StopSpellEffectPayload::write, StopSpellEffectPayload::read);

    public StopSpellEffectPayload(int entityId, SpellEffect spellEffect) {
        this(entityId, SpellEffects.REGISTRY.getEntry(spellEffect));
    }

    public static StopSpellEffectPayload read(RegistryByteBuf buf) {
        return new StopSpellEffectPayload(buf.readVarInt(), SpellEffects.ENTRY_PACKET_CODEC.decode(buf));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeVarInt(entityId);
        SpellEffects.ENTRY_PACKET_CODEC.encode(buf, entry);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public SpellEffect getSpellEffect() {
        return entry.value();
    }
}
