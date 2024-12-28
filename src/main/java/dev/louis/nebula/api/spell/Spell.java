package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.spell.fumble.SpellFumble;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface Spell<Caster> {

    /**
     * This should not be called manually unless you are a SpellCaster.
     */
    void cast(SpellSource<? extends Caster> source, TransactionContext context) throws SpellFumble;
}
