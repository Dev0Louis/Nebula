package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
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
public class SpellCastC2SPacket implements FabricPacket {
    private static final Identifier ID = new Identifier(Nebula.MOD_ID, "spellcast");
    public static final PacketType<SpellCastC2SPacket> TYPE = PacketType.create(ID, SpellCastC2SPacket::read);
    public final SpellType<?> spellType;

    public SpellCastC2SPacket(SpellType<?> spellType) {
        this.spellType = spellType;
    }

    public static SpellCastC2SPacket read(PacketByteBuf buf) {
        SpellType<?> spellType = buf.readRegistryValue(SpellType.REGISTRY);
        if (spellType == null) throw new IllegalStateException("Spell type not found in registry");
        return new SpellCastC2SPacket(spellType);
    }

    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(SpellType.REGISTRY, spellType);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void receive(SpellCastC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        player.server.executeSync(() -> player.getSpellManager().cast(packet.spellType));
    }

    @Override
    public String toString() {
        return "SpellCastC2SPacket[" +
                "spellType=" + spellType + ']';
    }

}
