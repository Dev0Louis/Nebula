package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Not a FabricPacket because of Restriction in the api.
 */
public record UpdateSpellCastabilityS2CPacket(Map<SpellType<? extends Spell>, Boolean> spells) implements NebulaPacket{
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "updatespellcastability");

    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeMap(
                spells,
                this::writeSpellType,
                PacketByteBuf::writeBoolean
        );
        return buf;
    }

    public static UpdateSpellCastabilityS2CPacket read(PacketByteBuf buf) {
        return new UpdateSpellCastabilityS2CPacket(readMapFromBuf(buf));
    }

    private static Map<SpellType<? extends Spell>, Boolean> readMapFromBuf(PacketByteBuf buf) {
        return buf.readMap(HashMap::new, UpdateSpellCastabilityS2CPacket::readSpellType, PacketByteBuf::readBoolean);
    }

    private static SpellType<?> readSpellType(PacketByteBuf buf) {
        return buf.readRegistryValue(Nebula.SPELL_REGISTRY);
    }

    public void writeSpellType(PacketByteBuf buf, SpellType<?> spellType) {
        buf.writeRegistryValue(Nebula.SPELL_REGISTRY, spellType);
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Nebula.runSyncWithBuf(
                client,
                buf,
                () -> client.player.getSpellManager().receiveSync(client, handler, buf, responseSender)
        );
    }

    public Identifier getId() {
        return ID;
    }
}
