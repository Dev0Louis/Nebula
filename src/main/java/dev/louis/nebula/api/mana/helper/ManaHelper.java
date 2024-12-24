package dev.louis.nebula.api.mana.helper;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public abstract class ManaHelper {
    private ManaHelper() {

    }

    public static boolean drainManaOrFail(ServerWorld world, ManaPoolHolder manaManagerHolder, int amount) {
        return drainManaOrFail(world, manaManagerHolder.getManaPool(), amount);
    }

    public static boolean drainManaOrFail(ServerWorld world, ManaPoolHolder manaManagerHolder, int amount, Transaction transaction) {
        return drainManaOrFail(world, manaManagerHolder.getManaPool(), amount, transaction);
    }

    public static boolean drainManaOrFail(ServerWorld world, ManaPool manaPool, int amount) {
        try(Transaction transaction = Transaction.openOuter()) {
            return drainManaOrFail(world, manaPool, amount, transaction);
        }
    }

    public static boolean drainManaOrFail(ServerWorld world, ManaPool manaPool, int amount, Transaction transaction) {
        var extracted = manaPool.extractMana(world, amount, transaction);
        return !(extracted < amount);
    }

    public static boolean drainManaOrFail(ServerWorld world, Optional<ManaPool> oManaPool, int amount) {
        if (oManaPool.isEmpty()) return false;
        return drainManaOrFail(world, oManaPool.get(), amount);
    }

    public static boolean drainManaOrFail(ServerWorld world, Optional<ManaPool> oManaPool, int amount, Transaction transaction) {
        if (oManaPool.isEmpty()) return false;
        return drainManaOrFail(world, oManaPool.get(), amount, transaction);
    }
}
