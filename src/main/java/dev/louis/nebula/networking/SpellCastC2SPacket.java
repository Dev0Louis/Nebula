package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.event.SpellCastCallback;
import dev.louis.nebula.spell.PlayerSpell;
import dev.louis.nebula.spell.Spell;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record SpellCastC2SPacket(PlayerSpell playerSpell) implements FabricPacket {
    public static final PacketType<SynchronizeManaAmountS2CPacket> PACKET_TYPE = PacketType.create(new Identifier(Nebula.MOD_ID, "spellcast"), SynchronizeManaAmountS2CPacket::new);
    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE, playerSpell.getType());
        playerSpell.writeBuf(buf);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public static SpellCastC2SPacket read(ServerPlayerEntity caster, PacketByteBuf buf) {
        Spell<?> spell = buf.readRegistryValue(Nebula.NebulaRegistries.SPELL_TYPE).create(caster);
        if(spell instanceof PlayerSpell playerSpell) {
            playerSpell.readBuf(buf);
            return new SpellCastC2SPacket(playerSpell);
        }else {
            caster.networkHandler.disconnect(Text.of("You can't cast a non-player spell!"));
            return null;
        }
    }
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SpellCastC2SPacket packet = SpellCastC2SPacket.read(player, buf);
        Objects.requireNonNull(packet);
        PlayerSpell playerSpell = packet.playerSpell();
        if (SpellCastCallback.EVENT.invoker().interact(player, playerSpell) == ActionResult.PASS) {
            player.getSpellManager().cast(playerSpell);
        }
    }
    public static Identifier getId() {
        return PACKET_TYPE.getId();
    }
}
