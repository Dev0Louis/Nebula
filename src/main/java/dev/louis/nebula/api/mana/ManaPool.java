package dev.louis.nebula.api.mana;

import dev.louis.nebula.mana.SimpleManaContainer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;

/**
 * A ManaContainer can store capacity, give capacity,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool {
    ManaPool EMPTY = new Empty();

    float capacity();
    float getMana();
    float insertMana(float amount, TransactionContext context);
    float extractMana(float amount, TransactionContext context);

    void readNbt(NbtCompound nbtCompound);
    void writeNbt(NbtCompound nbtCompound);

    @ApiStatus.Experimental
    static ManaPool createSimple(int baseMana, int maxMana) {
        return new SimpleManaContainer(baseMana, maxMana);
    }

    @ApiStatus.Internal
    final class Empty implements ManaPool {
        @Override
        public float capacity() {
            return 0;
        }

        @Override
        public float getMana() {
            return 0;
        }

        @Override
        public float insertMana(float amount, TransactionContext context) {
            return 0;
        }

        @Override
        public float extractMana(float amount, TransactionContext context) {
            return 0;
        }

        @Override
        public void readNbt(NbtCompound nbtCompound) {
            throw new UnsupportedOperationException("Empty ManaPool is not supposed to be read from nbt.");
        }

        @Override
        public void writeNbt(NbtCompound nbtCompound) {
            throw new UnsupportedOperationException("Empty ManaPool is not supposed to be written to nbt.");
        }
    }
}
