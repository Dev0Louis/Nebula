package dev.louis.nebula.api.mana.pool;

import dev.louis.nebula.api.mana.consumer.ManaConsumer;
import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;

/**
 * A ManaContainer can store capacity, give capacity,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool extends ManaSource, ManaConsumer {
    float getMana();

    default float insertMana(ServerWorld world, float insertion) {
        try (var t1 = Transaction.openOuter()) {
            float returnValue = insertMana(world, insertion, t1);
            t1.commit();
            return returnValue;
        }
    }

    float insertMana(ServerWorld world, float insertion, TransactionContext context);

    default float extractMana(ServerWorld world, float extraction) {
        try (var t1 = Transaction.openOuter()) {
            float returnValue = extractMana(world, extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    float extractMana(ServerWorld world, float extraction, TransactionContext context);

}
