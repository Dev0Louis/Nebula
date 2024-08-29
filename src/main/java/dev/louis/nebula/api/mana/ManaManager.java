package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface ManaManager extends ManaHolder {

    /**
     * @return The current amount of available mana.
     */
    float getMana();

    /**
     * Adds the specified amount of mana.
     * @param mana The amount of mana to add.
     */
    float insert(float mana, TransactionContext context);

    /**
     * Drains the specified amount of mana.
     * @param amount The amount of mana to drain.
     */
    float extract(float amount, TransactionContext context);
    
    /**
     * Querys the ManaManager's state to be synced to the client.
     * @return If the state was successfully send.
     */
    void querySync();

    /**
     * Sends the ManaManager's state to the client.
     * @return If the state was successfully send.
     */
    boolean sendSync();
}
