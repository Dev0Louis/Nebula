package dev.louis.nebula.networking;

import dev.louis.nebula.Nebula;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SynchronizeManaAmountS2CPacket {
    public static final Identifier ID = new Identifier(Nebula.MOD_ID, "spellcast");

    private final int mana;

    public SynchronizeManaAmountS2CPacket(int mana) {
        this.mana = mana;
    }
    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeVarInt(mana);
        return buf;
    }

    public static SynchronizeManaAmountS2CPacket read(PacketByteBuf buf) {
        return new SynchronizeManaAmountS2CPacket(buf.readVarInt());
    }
    public int mana() {
        return mana;
    }
}
