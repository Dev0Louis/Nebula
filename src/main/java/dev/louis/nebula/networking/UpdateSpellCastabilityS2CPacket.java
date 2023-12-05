package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static dev.louis.nebula.NebulaClient.runWithBuf;

public record UpdateSpellCastabilityS2CPacket(Map<SpellType<? extends Spell>, Boolean> spells) implements FabricPacket {
    public static final PacketType<UpdateSpellCastabilityS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "updatespellcastability"), UpdateSpellCastabilityS2CPacket::new);

    private UpdateSpellCastabilityS2CPacket(PacketByteBuf buf) {
        this(readMapFromBuf(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(spells.size());
        spells.forEach((spellType, knows) -> {
            buf.writeRegistryValue(Nebula.SPELL_REGISTRY, spellType);
            buf.writeBoolean(knows);
        });
    }

    public static UpdateSpellCastabilityS2CPacket create(PlayerEntity player) {
        Map<SpellType<? extends Spell>, Boolean> map = new HashMap<>();
        Nebula.SPELL_REGISTRY.forEach(spellType -> map.put(spellType, spellType.hasLearned(player)));
        return new UpdateSpellCastabilityS2CPacket(map);
    }

    public static UpdateSpellCastabilityS2CPacket readBuf(PacketByteBuf buf) {
        return new UpdateSpellCastabilityS2CPacket(readMapFromBuf(buf));
    }

    private static Map<SpellType<? extends Spell>, Boolean> readMapFromBuf(PacketByteBuf buf) {
        Map<SpellType<? extends Spell>, Boolean> spells = new HashMap<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            SpellType<? extends Spell> spellType = buf.readRegistryValue(Nebula.SPELL_REGISTRY);
            boolean knows = buf.readBoolean();
            spells.put(spellType, knows);
        }
        return spells;
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        runWithBuf(client, buf, () -> client.player.getSpellManager().receiveSync(client, handler, buf, responseSender));
    }

    public static Identifier getID() {
        return PACKET_TYPE.getId();
    }
}
