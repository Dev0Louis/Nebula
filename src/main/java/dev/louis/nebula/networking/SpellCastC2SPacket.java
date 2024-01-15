package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Not a FabricPacket because of Restriction in the api.
 */
public record SpellCastC2SPacket(Spell spell) implements NebulaPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "spellcast");

    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeRegistryValue(Nebula.SPELL_REGISTRY, spell.getType());
        spell.writeCastBuf(buf);
        return buf;
    }

    public static SpellCastC2SPacket read(ServerPlayerEntity caster, PacketByteBuf buf) {
        Spell spell = buf.readRegistryValue(Nebula.SPELL_REGISTRY).create(caster);
        spell.readCastBuf(buf);
        return new SpellCastC2SPacket(spell);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Nebula.runSyncWithBuf(
                server,
                buf,
                () -> {
                    SpellCastC2SPacket packet = SpellCastC2SPacket.read(player, buf);
                    player.getSpellManager().cast(packet.spell());
                }
        );
    }

    public Identifier getId() {
        return ID;
    }
}
