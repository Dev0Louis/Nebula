package dev.louis.nebula.api.spell;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * This class represents an attempt to cast a spell. It holds a reference to the caster of the Spell.
 *
 */
public interface Spell<Caster extends SpellCaster<Caster>> {

    /**
     * This should not be called manually unless you are //TODO: Add stuff.
     */
    @SuppressWarnings("RedundantThrows")
    default void cast(Caster caster, Transaction transaction) throws SpellException {

    }

    default void fail(Caster caster) {

    }
}
