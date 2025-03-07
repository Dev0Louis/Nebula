package dev.louis.nebula.api.mana.source;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface ManaSource {

    default long extractThaum(long extraction) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = extractThaum(extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    long extractThaum(long extraction, TransactionContext context);
}
