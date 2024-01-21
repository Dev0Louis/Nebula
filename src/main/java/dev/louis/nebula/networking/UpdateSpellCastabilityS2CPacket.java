package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record UpdateSpellCastabilityS2CPacket(Map<SpellType<? extends Spell>, Boolean> spells) implements FabricPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "updatespellcastability");
    public static final PacketType<UpdateSpellCastabilityS2CPacket> TYPE = PacketType.create(ID, UpdateSpellCastabilityS2CPacket::read);

    public static UpdateSpellCastabilityS2CPacket read(PacketByteBuf buf) {
        return new UpdateSpellCastabilityS2CPacket(readMapFromBuf(buf));
    }

    private static Map<SpellType<? extends Spell>, Boolean> readMapFromBuf(PacketByteBuf buf) {
        return buf.readMap(HashMap::new, UpdateSpellCastabilityS2CPacket::readSpellType, PacketByteBuf::readBoolean);
    }

    private static SpellType<?> readSpellType(PacketByteBuf buf) {
        return buf.readRegistryValue(SpellType.REGISTRY);
    }

    public void write(PacketByteBuf buf) {
        buf.writeMap(
                spells,
                this::writeSpellType,
                PacketByteBuf::writeBoolean
        );
    }

    public void writeSpellType(PacketByteBuf buf, SpellType<?> spellType) {
        buf.writeRegistryValue(SpellType.REGISTRY, spellType);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
