package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Not a FabricPacket because of Restriction in the api.
 */
public record SpellCastC2SPacket(Spell spell) implements FabricPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "spellcast");
    public static final PacketType<SpellCastC2SPacket> TYPE = PacketType.create(ID, SpellCastC2SPacket::read);

    public static SpellCastC2SPacket read(PacketByteBuf buf) {
        SpellType<?> spellType = buf.readRegistryValue(SpellType.REGISTRY);
        if(spellType == null) throw new IllegalStateException("Spell type not found in registry");
        Spell spell = spellType.create();
        return new SpellCastC2SPacket(spell);
    }

    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(SpellType.REGISTRY, spell.getType());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void receive(SpellCastC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        var spell = packet.spell();
        spell.setCaster(player);
        player.server.executeSync(() -> {
            player.getSpellManager().cast(spell);
        });
    }
}
