package dev.louis.nebula.networking.s2c;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record UpdateSpellCastabilityPayload(Map<SpellType<? extends Spell>, Boolean> spells) implements CustomPayload {
    public static final CustomPayload.Id<UpdateSpellCastabilityPayload> ID = new Id<>(Identifier.of(Nebula.MOD_ID, "updatespellcastability"));
    public static final PacketCodec<RegistryByteBuf, UpdateSpellCastabilityPayload> CODEC = PacketCodec.of(
            UpdateSpellCastabilityPayload::write,
            UpdateSpellCastabilityPayload::read
    );

    private void write(RegistryByteBuf registryByteBuf) {
        writeMap(
                registryByteBuf,
                spells,
                UpdateSpellCastabilityPayload::writeSpellType,
                PacketByteBuf::writeBoolean
        );
    }

    public static UpdateSpellCastabilityPayload read(RegistryByteBuf buf) {
        return new UpdateSpellCastabilityPayload(readMapFromBuf(buf));
    }

    private static Map<SpellType<? extends Spell>, Boolean> readMapFromBuf(RegistryByteBuf buf) {
        return readHashMap(buf, UpdateSpellCastabilityPayload::readSpellType, PacketByteBuf::readBoolean);
    }

    private static SpellType<?> readSpellType(RegistryByteBuf buf) {
        return PacketCodecs.registryValue(SpellType.REGISTRY_KEY).decode(buf);
    }

    public static void writeSpellType(RegistryByteBuf buf, SpellType<?> spellType) {
        PacketCodecs.registryValue(SpellType.REGISTRY_KEY).encode(buf, spellType);
    }

    public static <K, V> HashMap<K, V> readHashMap(
            RegistryByteBuf byteBuf, PacketDecoder<? super RegistryByteBuf, K> keyReader, PacketDecoder<? super RegistryByteBuf, V> valueReader
    ) {
        int size = byteBuf.readVarInt();
        HashMap<K, V> map = new HashMap<>(size);

        for(int i = 0; i < size; ++i) {
            K object = keyReader.decode(byteBuf);
            V object2 = valueReader.decode(byteBuf);
            map.put(object, object2);
        }

        return map;
    }

    public static void writeMap(RegistryByteBuf byteBuf, Map<SpellType<?>, Boolean> map, PacketEncoder<? super RegistryByteBuf, SpellType<?>> keyWriter, PacketEncoder<? super RegistryByteBuf, Boolean> valueWriter) {
        byteBuf.writeVarInt(map.size());
        map.forEach((key, value) -> {
            keyWriter.encode(byteBuf, key);
            valueWriter.encode(byteBuf, value);
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
