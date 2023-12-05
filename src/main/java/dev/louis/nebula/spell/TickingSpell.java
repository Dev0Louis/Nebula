package dev.louis.nebula.spell;

import net.minecraft.entity.player.PlayerEntity;

public class TickingSpell extends Spell {
    protected int spellAge = 0;
    private boolean shouldContinue = true;
    public TickingSpell(SpellType<? extends TickingSpell> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        this.getCaster().getSpellManager().startTickingSpell(this);
    }

    /**
     * This method is called every tick while shouldContinue() is true.
     * When the Spell is finished, call stop(). This will result in the Spell no longer getting Ticked.
     */
    public void tick() {
        spellAge++;
    }

    public void stop(boolean fromDeath) {
        shouldContinue = false;
    }

    /**
     * This method stops the spell!
     * The Spell will no longer be ticked.
     * Calling this method assumes the Caster is still alive.
     * If that is not the case call {@link #stop(boolean)}.
     */
    public void stop() {
        stop(false);
    }

    public boolean shouldContinue() {
        return shouldContinue;
    }
}
