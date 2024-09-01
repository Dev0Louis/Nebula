package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.mana.ManaManagerHolder;
import dev.louis.nebula.api.mana.ManaPool;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * This class represents an attempt to cast a spell. It holds a reference to the caster of the Spell.
 *
 */
public interface Spell<E extends SpellSource<?>> {

    /**
     * This should not be called manually unless you are //TODO: Add stuff.
     */
    void cast(E caster) throws SpellException;


    // Small utility methods to help to easily extract mana or throw an Exception if not enough Mana is available
    static void drainMana(ManaManagerHolder manaManagerHolder, int amount) throws SpellException {
        drainMana(manaManagerHolder.getManaManager(), amount);
    }

    static void drainMana(ManaManagerHolder manaManagerHolder, int amount, Transaction transaction) throws SpellException {
        drainMana(manaManagerHolder.getManaManager(), amount, transaction);
    }

    static void drainMana(ManaPool manaPool, int amount) throws SpellException {
        try(Transaction transaction = Transaction.openOuter()) {
            drainMana(manaPool, amount, transaction);
        }
    }

    static void drainMana(ManaPool manaPool, int amount, Transaction transaction) throws SpellException {
        var extracted = manaPool.extract(amount, transaction);
        if (extracted < amount) throw SpellException.create();
        transaction.commit();
    }
}
