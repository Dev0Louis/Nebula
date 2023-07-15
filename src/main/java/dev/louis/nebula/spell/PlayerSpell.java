package dev.louis.nebula.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public abstract class PlayerSpell extends Spell<PlayerEntity> {
    public PlayerSpell(SpellType<? extends PlayerSpell> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    public PacketByteBuf readBuf(PacketByteBuf buf) {
        return buf;
    }

    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        return buf;
    }

}
