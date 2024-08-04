package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A ManaContainer can store mana, give mana,
 */
public interface ManaHolder {
    int capacity();
    int mana();
    void setMana(int mana);
    int insert(int amount, TransactionContext context);
}
