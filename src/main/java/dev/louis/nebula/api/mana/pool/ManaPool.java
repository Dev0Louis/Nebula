package dev.louis.nebula.api.mana.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.louis.nebula.api.mana.consumer.ManaConsumer;
import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

/**
 * A ManaContainer can store capacity, give capacity,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool extends ManaSource, ManaConsumer {
    float getMana();
    float getCapacity();

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

    NbtCompound writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);
}
