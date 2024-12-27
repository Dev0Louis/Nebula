package dev.louis.nebula.api.mana.source;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;

public interface ManaSource {

    default long extractMana(long extraction) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = extractMana(extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    long extractMana(long extraction, TransactionContext context);
}
