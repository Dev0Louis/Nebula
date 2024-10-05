package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

@SuppressWarnings("UnusedReturnValue")
public interface ManaManager extends ManaPool {
    void setMana(float mana);

    void setMana(float mana, TransactionContext transaction);

    /**
     * Querys the ManaManager's state to be synced to the client.
     */
    void checkSync();

    /**
     * Sends the ManaManager's state to the client.
     * @return If the state was successfully send.
     */
    boolean sendSync();
}
