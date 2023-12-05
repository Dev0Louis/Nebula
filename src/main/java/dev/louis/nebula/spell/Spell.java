package dev.louis.nebula.spell;

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

    public Identifier getID() {
        return this.getType().getId();
    }

    public PlayerEntity getCaster() {
        return this.caster;
    }

    public SpellType<? extends Spell> getType() {
        return this.spellType;
    }

    public boolean isCastable() {
        return this.getType().isCastable(this.caster);
    }

    public PacketByteBuf readBuf(PacketByteBuf buf) {
        return buf;
    }

    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        return buf;
    }

    public void drainMana() {
        getCaster().getManaManager().drainMana(getType().getManaCost());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.getID().toString() + "]";
    }
}
