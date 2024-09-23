package dev.louis.nebula.api.spell;

import dev.louis.nebula.api.event.SpellCastCallback;
import dev.louis.nebula.api.mana.ManaPool;
import dev.louis.nebula.api.mana.holder.ManaManagerHolder;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;


public interface Spell<Caster> {

    default boolean tryCast(SpellSource<Caster> source) {
        var allowed = SpellCastCallback.BEFORE.invoker().allowSpellCast(source, this);
        if (!allowed) return false;

        try {
            cast(source);
        } catch (SpellException e) {
            e.onFail(source);
            return false;
        }

        SpellCastCallback.AFTER.invoker().onSpellCast(source, this);
        return true;
    }

    /**
     * This should not be called manually unless you are //TODO: Add stuff.
     */
    void cast(SpellSource<Caster> source) throws SpellException;


    // Small utility methods to help to easily extract capacity or throw an Exception if not enough Mana is available
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
        var extracted = manaPool.extractMana(amount, transaction);
        if (extracted < amount) throw SpellException.create();
        transaction.commit();
    }
}
