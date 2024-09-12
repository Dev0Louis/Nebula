package dev.louis.nebula.api.mana;

import dev.louis.nebula.mana.SimpleManaContainer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

/**
 * A ManaContainer can store mana, give mana,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool {
    ManaPool EMPTY = new ManaPool() {
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

        }

        @Override
        public void writeNbt(NbtCompound nbtCompound) {

        }
    };

    float capacity();
    float getMana();
    float insertMana(float amount, TransactionContext context);
    float extractMana(float amount, TransactionContext context);

    void readNbt(NbtCompound nbtCompound);
    void writeNbt(NbtCompound nbtCompound);

    static ManaPool createSimple(int startingValue, int maxValue) {
        return new SimpleManaContainer(startingValue, maxValue);
    }
}
