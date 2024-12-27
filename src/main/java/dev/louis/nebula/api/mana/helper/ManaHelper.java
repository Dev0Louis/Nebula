package dev.louis.nebula.api.mana.helper;

import dev.louis.nebula.api.mana.pool.ManaPool;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class ManaHelper {
    private static final BigDecimal BIG1000 = BigDecimal.TEN.pow(3);
    private ManaHelper() {

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

    public static String formatKilomana(long mana) {
        if (mana == Long.MAX_VALUE) {
            return "∞";
        } else if (mana == Long.MIN_VALUE) {
            return "-∞";
        } else {
            return new BigDecimal(mana).divide(BIG1000).toString();
        }
    }
}
