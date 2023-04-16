package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record SynchronizeSpellKnowledgeS2CPacket(Map<SpellType<? extends Spell>, Boolean> spells) implements FabricPacket {
    public static final PacketType<SynchronizeManaAmountS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "syncspellknowledge"), SynchronizeManaAmountS2CPacket::new);

    public SynchronizeSpellKnowledgeS2CPacket(PacketByteBuf buf) {
        this(readMap(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(spells.size());
        spells.forEach((spellType, knows) -> {
            buf.writeRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE, spellType);
            buf.writeBoolean(knows);
        });
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static Identifier getID() {
        return PACKET_TYPE.getId();
    }

    public static SynchronizeSpellKnowledgeS2CPacket create(PlayerEntity player) {
        Map<SpellType<? extends Spell>, Boolean> map = new HashMap<>();
        Nebula.NebulaRegistries.SPELL_TYPE.forEach(spellType -> {
            map.put(spellType, spellType.doesKnow(player));
        });
        return new SynchronizeSpellKnowledgeS2CPacket(map);
    }

    public static SynchronizeSpellKnowledgeS2CPacket read(PacketByteBuf buf) {
        return new SynchronizeSpellKnowledgeS2CPacket(buf);
    }

    private static Map<SpellType<? extends Spell>, Boolean> readMap(PacketByteBuf buf) {
        Map<SpellType<? extends Spell>, Boolean> spells = new HashMap<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            SpellType<? extends Spell> spellType = buf.readRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE);
            boolean knows = buf.readBoolean();
            spells.put(spellType, knows);
        }
        return spells;
    }

    public Map<SpellType<? extends Spell>, Boolean> spells() {
        return spells;
    };
}
