package dev.louis.nebula.api.mana.pool;

import dev.louis.nebula.api.mana.source.ManaSource;
import dev.louis.nebula.mana.SimpleManaContainer;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

/**
 * A ManaContainer can store capacity, give capacity,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool extends ManaSource {
    float getCapacity();
    float getMana();

    default float insertMana(float insertion) {

        try (var t1 = Transaction.openOuter()) {
            float returnValue = insertMana(insertion, t1);
            t1.commit();
            return returnValue;
        }
    }

    float insertMana(float insertion, TransactionContext context);

    default float extractMana(float extraction) {
        try (var t1 = Transaction.openOuter()) {
            float returnValue = extractMana(extraction, t1);
            t1.commit();
            return returnValue;
        }
    }

    float extractMana(float extraction, TransactionContext context);

    void readNbt(NbtCompound nbtCompound);
    void writeNbt(NbtCompound nbtCompound);

    static ManaPool createSimple(int baseMana, int maxMana) {
        return new SimpleManaContainer(baseMana, maxMana);
    }
}
