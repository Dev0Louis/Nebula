package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;


public interface Spell<Caster> {

    /**
     * This should not be called manually unless you are a SpellCaster.
     */
    void cast(SpellSource<Caster> source) throws SpellException;


    // Small utility methods to help to easily extract capacity or throw an Exception if not enough Mana is available
    static void drainMana(ManaPoolHolder manaManagerHolder, int amount) throws SpellException {
        drainMana(manaManagerHolder.getManaPool(), amount);
    }

    static void drainMana(ManaPoolHolder manaManagerHolder, int amount, Transaction transaction) throws SpellException {
        drainMana(manaManagerHolder.getManaPool(), amount, transaction);
    }

    static void drainMana(ManaPool manaPool, int amount) throws SpellException {
        try(Transaction transaction = Transaction.openOuter()) {
            drainMana(manaPool, amount, transaction);
            transaction.commit();
        }
    }

    static void drainMana(ManaPool manaPool, int amount, Transaction transaction) throws SpellException {
        var extracted = manaPool.extractMana(amount, transaction);
        if (extracted < amount) throw SpellException.create();
    }
}
