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

import static dev.louis.nebula.NebulaClient.runSyncWithBuf;

public record UpdateSpellCastabilityS2CPacket(Map<SpellType<? extends Spell>, Boolean> spells) implements FabricPacket {
    public static final PacketType<UpdateSpellCastabilityS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "updatespellcastability"), UpdateSpellCastabilityS2CPacket::new);

    private UpdateSpellCastabilityS2CPacket(PacketByteBuf buf) {
        this(readMapFromBuf(buf));
    }

    public static void writeSpellType(PacketByteBuf buf, SpellType<?> spellType) {
        buf.writeRegistryValue(Nebula.SPELL_REGISTRY, spellType);
    }

    private static SpellType<?> readSpellType(PacketByteBuf buf) {
        return buf.readRegistryValue(Nebula.SPELL_REGISTRY);
    }

    private static Map<SpellType<? extends Spell>, Boolean> readMapFromBuf(PacketByteBuf buf) {
        return buf.readMap(HashMap::new, UpdateSpellCastabilityS2CPacket::readSpellType, PacketByteBuf::readBoolean);
    }

    public static UpdateSpellCastabilityS2CPacket read(PacketByteBuf buf) {
        return new UpdateSpellCastabilityS2CPacket(buf);
    }

    public void write(PacketByteBuf buf) {
        buf.writeMap(
                spells,
                UpdateSpellCastabilityS2CPacket::writeSpellType,
                PacketByteBuf::writeBoolean
        );
    }

    public static UpdateSpellCastabilityS2CPacket create(PlayerEntity player) {
        Map<SpellType<? extends Spell>, Boolean> map = new HashMap<>();
        Nebula.SPELL_REGISTRY.forEach(spellType -> map.put(spellType, spellType.hasLearned(player)));
        return new UpdateSpellCastabilityS2CPacket(map);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        runSyncWithBuf(client, buf, () -> client.player.getSpellManager().receiveSync(client, handler, buf, responseSender));
    }

    public static Identifier getID() {
        return PACKET_TYPE.getId();
    }
}
