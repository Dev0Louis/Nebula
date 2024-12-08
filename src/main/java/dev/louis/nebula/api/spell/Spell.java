package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.event.SpellCastEvent;

public interface Spell<Caster> {

    default boolean tryCast(SpellSource<? extends Caster> source) {
        var allowed = SpellCastEvent.BEFORE.invoker().allowSpellCast(source, this);
        if (!allowed) return false;

        if (cast(source)) {
            SpellCastEvent.AFTER.invoker().onSpellCast(source, this);
            return true;
        }

        return false;
    }

    /**
     * This should not be called manually unless you are a SpellCaster.
     */
    boolean cast(SpellSource<? extends Caster> source);
}
