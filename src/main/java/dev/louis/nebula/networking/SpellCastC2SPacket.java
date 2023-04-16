package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SpellCastC2SPacket(Spell spell) implements FabricPacket {
    public static final PacketType<SynchronizeManaAmountS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "spellcast"), SynchronizeManaAmountS2CPacket::new);
    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE, spell.getType());
        spell.writeBuf(buf);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static SpellCastC2SPacket read(PlayerEntity caster, PacketByteBuf buf) {
        Spell spell = buf.readRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE).create(caster);
        spell.readBuf(buf);
        return new SpellCastC2SPacket(spell);
    }
    public static Identifier getId() {
        return PACKET_TYPE.getId();
    }
}
