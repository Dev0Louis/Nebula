package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A ManaContainer can store mana, give mana,
 */
@SuppressWarnings("UnusedReturnValue")
public interface ManaPool {
    float capacity();
    float getMana();
    void setMana(float mana);
    float insertMana(float amount, TransactionContext context);
    float extractMana(float amount, TransactionContext context);
}
