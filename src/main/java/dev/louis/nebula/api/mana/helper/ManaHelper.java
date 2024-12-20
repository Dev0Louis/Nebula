package dev.louis.nebula.api.mana.helper;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.Optional;

public abstract class ManaHelper {
    public static boolean drainManaOrFail(ManaPoolHolder manaManagerHolder, int amount) {
        return drainManaOrFail(manaManagerHolder.getManaPool(), amount);
    }

    public static boolean drainManaOrFail(ManaPoolHolder manaManagerHolder, int amount, Transaction transaction) {
        return drainManaOrFail(manaManagerHolder.getManaPool(), amount, transaction);
    }

    public static boolean drainManaOrFail(ManaPool manaPool, int amount) {
        try(Transaction transaction = Transaction.openOuter()) {
            return drainManaOrFail(manaPool, amount, transaction);
        }
    }

    public static boolean drainManaOrFail(ManaPool manaPool, int amount, Transaction transaction) {
        var extracted = manaPool.extractMana(amount, transaction);
        return !(extracted < amount);
    }

    public static boolean drainManaOrFail(Optional<ManaPool> oManaPool, int amount) {
        if (oManaPool.isEmpty()) return false;
        return drainManaOrFail(oManaPool.get(), amount);
    }

    public static boolean drainManaOrFail(Optional<ManaPool> oManaPool, int amount, Transaction transaction) {
        if (oManaPool.isEmpty()) return false;
        return drainManaOrFail(oManaPool.get(), amount, transaction);
    }
}
