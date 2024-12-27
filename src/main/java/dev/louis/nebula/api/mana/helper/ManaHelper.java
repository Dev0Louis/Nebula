package dev.louis.nebula.api.mana.helper;

import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.math.BigDecimal;

public abstract class ManaHelper {
    private static final BigDecimal BIG1000 = BigDecimal.TEN.pow(3);
    private ManaHelper() {

    }

    public static boolean drainKilomanaOrFail(ManaSource manaSource, long amount) {
        return drainManaOrFail(manaSource, amount * 1000L);
    }

    public static boolean drainManaOrFail(ManaSource manaSource, long amount) {
        try(Transaction transaction = Transaction.openOuter()) {
            return drainManaOrFail(manaSource, amount, transaction);
        }
    }

    public static boolean drainKilomanaOrFail(ManaSource manaSource, long amount, Transaction transaction) {
        return drainManaOrFail(manaSource, amount * 1000L, transaction);
    }

    public static boolean drainManaOrFail(ManaSource manaSource, long amount, Transaction transaction) {
        var extracted = manaSource.extractMana(amount, transaction);
        return !(extracted < amount);
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
