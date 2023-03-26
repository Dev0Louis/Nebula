package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SpellCastC2SPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "spellcast");

    private final Spell spell;

    public SpellCastC2SPacket(Spell spell) {
        this.spell = spell;
    }
    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE, spell.getType());
        spell.writeBuf(buf);
        return buf;
    }

    public static SpellCastC2SPacket read(PlayerEntity caster, PacketByteBuf buf) {
        Spell spell = buf.readRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE).create(caster);
        spell.readBuf(buf);
        return new SpellCastC2SPacket(spell);
    }

    public Spell spell() {
        return spell;
    }
}
