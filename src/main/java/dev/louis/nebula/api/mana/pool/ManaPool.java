package dev.louis.nebula.api.mana.pool;

import dev.louis.nebula.api.mana.consumer.ManaConsumer;
import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.mana.SimpleManaPool;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

/**
 * A ManaContainer can store capacity, give capacity,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool extends ManaSource, ManaConsumer {
    long getThaum();
    long getThaumCapacity();

    default long insertThaum(long insertion) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = insertThaum(insertion, t1);
            t1.commit();
            return returnValue;
        }
    }

    long insertThaum(long insertion, TransactionContext context);

    default long extractThaum(long extraction) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = extractThaum(extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    long extractThaum(long extraction, TransactionContext context);

    NbtCompound writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);

    static ManaPool createSimple(long baseThaum, long maxThaum) {
        return new SimpleManaPool(baseThaum, maxThaum);
    }

}
