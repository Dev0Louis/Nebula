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
    long getMana();
    long getCapacity();

    default long insertMana(long insertion) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = insertMana(insertion, t1);
            t1.commit();
            return returnValue;
        }
    }

    long insertMana(long insertion, TransactionContext context);

    default long extractMana(long extraction) {
        try (var t1 = Transaction.openOuter()) {
            long returnValue = extractMana(extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    long extractMana(long extraction, TransactionContext context);

    NbtCompound writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);

    static ManaPool createSimple(long baseMana, long maxMana) {
        return new SimpleManaPool(baseMana, maxMana);
    }

}
