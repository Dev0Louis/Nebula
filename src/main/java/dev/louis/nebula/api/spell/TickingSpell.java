package dev.louis.nebula.api.spell;

import java.util.Collection;

public interface TickingSpell<Caster extends SpellSource<?>> extends Spell<Caster> {
    /**
     * This should not be called manually unless you are //TODO: Add stuff.
     */
    void tick(Caster caster);

    boolean shouldContinue(Caster caster);

    default boolean canStart(Caster caster, Collection<TickingSpell<?>> activeSpells) {
        return activeSpells.stream().anyMatch(tickingSpell -> tickingSpell.getType() == this.getType());
    }

    void onEnd(Caster caster);

    TickingSpellType<TickingSpell<Caster>> getType();

    //void writeNbt(NbtCompound nbt);
    //void readNbt(NbtCompound nbt);
}
