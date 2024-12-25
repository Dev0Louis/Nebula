package dev.louis.nebula.api.mana.consumer;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;

public interface ManaConsumer {
    default float insertMana(float insertion) {
        try (var t1 = Transaction.openOuter()) {
            float returnValue = insertMana(insertion, t1);
            t1.commit();
            return returnValue;
        }
    }

    float insertMana(float insertion, TransactionContext context);
}
