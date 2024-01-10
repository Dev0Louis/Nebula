package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.spell.Spell;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record SpellCastC2SPacket(Spell spell) implements FabricPacket {
    public static final PacketType<SynchronizeManaAmountS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "spellcast"), SynchronizeManaAmountS2CPacket::new);

    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(Nebula.SPELL_REGISTRY, spell.getType());
        spell.writeCastBuf(buf);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static SpellCastC2SPacket read(ServerPlayerEntity caster, PacketByteBuf buf) {
        Spell spell = buf.readRegistryValue(Nebula.SPELL_REGISTRY).create(caster);
        spell.readCastBuf(buf);
        return new SpellCastC2SPacket(spell);
    }

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SpellCastC2SPacket packet = SpellCastC2SPacket.read(player, buf);
        Objects.requireNonNull(packet);
        Spell spell = packet.spell();
        player.getSpellManager().cast(spell);
    }

    public static Identifier getId() {
        return PACKET_TYPE.getId();
    }
}
