package dev.louis.nebula.api.mana.consumer;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface ManaConsumer {
    default long insertThaum(long insertion) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = insertThaum(insertion, t1);
            t1.commit();
            return returnValue;
        }
    }

    long insertThaum(long insertion, TransactionContext context);
}
