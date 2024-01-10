package dev.louis.nebula.spell;

import dev.louis.nebula.spell.manager.SpellManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * This class represents an attempt to cast a spell. It holds a reference to the caster of the Spell.
 */
public abstract class Spell {
    private final SpellType<? extends Spell> spellType;
    private final PlayerEntity caster;


    public Spell(SpellType<? extends Spell> spellType, PlayerEntity caster) {
        this.spellType = spellType;
        this.caster = caster;
    }

    /**
     * Is called after {@link Spell#isCastable()} if the return of the method is true.
     * This should not be called manually.
     * Use {@link SpellManager#cast(Spell)} or {@link SpellManager#cast(SpellType)}
     */
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

    /**
     * If true {@link Spell#applyCost()} and {@link Spell#cast()}  will be called in that order. <br>
     * If false nothing will be called.
     */
    public boolean isCastable() {
        return this.getType().isCastable(this.caster);
    }

    /**
     * Read additional casting data about the spell from the buf.
     * @param buf The buf to be read from.
     * @return The buf after being read from.
     */
    public PacketByteBuf readCastBuf(PacketByteBuf buf) {
        return buf;
    }

    /**
     * Write additional casting data about the spell to the buf.
     * @param buf The buf to be written to.
     * @return The buf after being written to.
     */
    public PacketByteBuf writeCastBuf(PacketByteBuf buf) {
        return buf;
    }

    /**
     * Read additional response data about the spell from the buf.
     * @param buf The buf to be read from.
     * @return The buf after being read from.
     */
    public PacketByteBuf readResponseBuf(PacketByteBuf buf) {
        return readCastBuf(buf);
    }

    /**
     * Write additional response data about the spell to the buf.
     * @param buf The buf to be written to.
     * @return The buf after being written to.
     */
    public PacketByteBuf writeResponseBuf(PacketByteBuf buf) {
        return writeCastBuf(buf);
    }

    /**
     * Remove the Cost required by the SpellType.
     */
    public void applyCost() {
        getCaster().getManaManager().drainMana(getType().getManaCost());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.getID().toString() + "]";
    }
}
