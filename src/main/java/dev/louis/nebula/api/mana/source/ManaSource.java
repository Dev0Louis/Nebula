package dev.louis.nebula.api.mana.source;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface ManaSource {

    default float extractMana(float extraction) {
        try (var t1 = Transaction.openOuter()) {
            float returnValue = extractMana(extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    float extractMana(float extraction, TransactionContext context);
}
