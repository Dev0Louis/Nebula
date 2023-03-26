package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SynchronizeSpellKnowledgeS2CPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "syncspellknowledge");

    private final Map<SpellType<? extends Spell>, Boolean> spells;

    public SynchronizeSpellKnowledgeS2CPacket(Map<SpellType<? extends Spell>, Boolean> spells) {
        this.spells = Map.copyOf(spells);
    }
    private SynchronizeSpellKnowledgeS2CPacket(PacketByteBuf buf) {
        Map<SpellType<? extends Spell>, Boolean> map = new HashMap<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            SpellType<? extends Spell> spellType = buf.readRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE);
            boolean knows = buf.readBoolean();
            map.put(spellType, knows);
        }
        this.spells = map;
    }

    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeVarInt(spells.size());
        spells.forEach((spellType, knows) -> {
            buf.writeRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE, spellType);
            buf.writeBoolean(knows);
        });
        return buf;
    }

    public static SynchronizeSpellKnowledgeS2CPacket create() {
        Map<SpellType<? extends Spell>, Boolean> map = new HashMap<>();
        Nebula.NebulaRegistries.SPELL_TYPE.forEach(spellType -> {
            map.put(spellType, true);
        });
        return new SynchronizeSpellKnowledgeS2CPacket(map);
    }

    public static SynchronizeSpellKnowledgeS2CPacket read(PacketByteBuf buf) {
        return new SynchronizeSpellKnowledgeS2CPacket(buf);
    }

    public Map<SpellType<? extends Spell>, Boolean> spells() {
        return spells;
    };
}
