package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.manager.spell.SpellManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * This class represents an attempt to cast a spell. It holds a reference to the caster of the Spell.
 *
 */
public abstract class Spell {
    private static final int DEFAULT_SPELL_AGE = 3 * 20;

    protected final SpellType<?> spellType;
    protected final PlayerEntity caster;

    protected boolean wasInterrupted;
    protected boolean stopped;

    public int age = 0;

    public Spell(SpellType<?> spellType, PlayerEntity caster) {
        this.spellType = spellType;
        this.caster = caster;
    }

    /**
     * Is called after {@link Spell#isCastable()} if the return of the method is true.
     * This should not be called manually.
     * Use {@link SpellManager#cast(Spell)} or {@link SpellManager#cast(SpellType)}
     */
    public abstract void cast();

    /**
     * Remove the Cost required by the SpellType.
     */
    public void applyCost() {
        this.getCaster().getManaManager().drainMana(getType().getManaCost());
    }

    public void tick() {
    }

    public Identifier getId() {
        return this.getType().getId();
    }

    public PlayerEntity getCaster() {
        return this.caster;
    }

    public SpellType<? extends Spell> getType() {
        return this.spellType;
    }

    public int getDuration() {
        return DEFAULT_SPELL_AGE;
    }

    /**
     * This method is called if the spell ends.<br>
     * After this method is called {@link Spell#tick()} will not be called anymore.<br>
     * Use this to finish all remaining logic of the spell.
     */
    public void finish() {
    }

    /**
     * Used to check if the spell should be stopped. <br>
     * It is important to check super or check if the spell was interrupted {@link Spell#wasInterrupted()}. <br>
     */
    public boolean shouldStop() {
        return this.stopped || this.age > this.getDuration();
    }

    /**
     * Interrupts the spell.<br>
     * This method is final as no Spell is immune to being interrupted<br>
     * <br>
     * If you want to show to the player that the spell was interrupted check {@link Spell#wasInterrupted()} in {@link Spell#finish()} as it is called after this.
     */
    public final void interrupt() {
        this.wasInterrupted = true;
        this.stop();
    }

    /**
     * Stop the spell.<br>
     * This method is final as post-spell action shall be handled in {@link Spell#finish()}<br>
     * <br>
     * Unlike {@link Spell#interrupt()} if you call this method it is expected that the spell has finished execution.<br>
     */
    protected final void stop() {
        this.stopped = true;
    }

    /**
     * If true {@link Spell#applyCost()} and {@link Spell#cast()}  will be called in that order. <br>
     * If false nothing will be called. <br>
     * {@link SpellType.Builder#castability(SpellType.Castability)} instead if possible.
     */
    public boolean isCastable() {
        return this.getType().isCastable(this.caster);
    }

    public boolean isClient() {
        return this.getCaster().getWorld().isClient();
    }

    public boolean wasInterrupted() {
        return this.wasInterrupted;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "[spellType=" + this.spellType +
                ", caster=" + this.caster +
                ", spellAge=" + this.age +
                ", wasInterrupted=" + this.wasInterrupted + "]";
    }
}
