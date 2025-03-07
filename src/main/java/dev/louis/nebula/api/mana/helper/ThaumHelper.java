package dev.louis.nebula.api.mana.helper;

import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.math.BigDecimal;

public abstract class ThaumHelper {
    private static final BigDecimal BIG1000 = BigDecimal.TEN.pow(3);
    private ThaumHelper() {

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
        var extracted = manaSource.extractThaum(amount, transaction);
        return !(extracted < amount);
    }

    public static String formatKilothaum(long thaum) {
        if (thaum == Long.MAX_VALUE) {
            return "∞";
        } else if (thaum == Long.MIN_VALUE) {
            return "-∞";
        } else {
            return new BigDecimal(thaum).divide(BIG1000).toString();
        }
    }
}
