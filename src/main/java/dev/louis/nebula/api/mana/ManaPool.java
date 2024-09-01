package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A ManaContainer can store mana, give mana,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool {
    float capacity();
    float get();
    void set(float mana);
    float insert(float amount, TransactionContext context);
    float extract(float amount, TransactionContext context);
}
