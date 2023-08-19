package dev.louis.nebula.spell;

import net.minecraft.entity.player.PlayerEntity;

public class MultiTickSpell extends Spell {
    protected int spellAge = 0;
    private boolean shouldRun = true;
    public MultiTickSpell(SpellType<? extends Spell> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        this.getCaster().getMultiTickSpells().add(this);
    }

    /**
     * This method is called every tick while shouldContinue() is true.
     * When the Spell is finished, call stop(). This will result in the Spell no longer getting Ticked.
     */
    public void tick() {
        spellAge++;
    };
    public void stop(boolean fromDeath) {
        shouldRun = false;
    }
    public void stop() {
        stop(false);
    }
    public boolean shouldContinue() {
        return shouldRun;
    };
}
