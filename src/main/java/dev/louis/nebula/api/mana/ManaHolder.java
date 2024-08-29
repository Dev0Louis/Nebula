package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A ManaContainer can store mana, give mana,
 */
public interface ManaHolder {
    int manaCapacity();
    float getMana();
    void setMana(float mana);
    float insert(float amount, TransactionContext context);
    float extract(float amount, TransactionContext context);
}
