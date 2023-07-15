package dev.louis.nebula.spell;

import dev.louis.nebula.api.NebulaUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class Spell {
    private final SpellType<? extends Spell> spellType;
    private final PlayerEntity caster;


    public Spell(SpellType<? extends Spell> spellType, PlayerEntity caster) {
        this.spellType = spellType;
        this.caster = caster;
    }

    public abstract void cast();
    public void drainMana() {
        NebulaUser.access(getCaster()).getManaManager().drainMana(getType().getManaCost());
    }
    public Identifier getID() {
        return getType().getId();
    }

    public PlayerEntity getCaster() {
        return caster;
    }
    public SpellType<? extends Spell> getType() {
        return spellType;
    }
    public PacketByteBuf readBuf(PacketByteBuf buf) {
        return buf;
    }

    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        return buf;
    }

    public boolean isCastable() {
        return getType().isCastable(caster);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getID().toString() + "]";
    }

}
