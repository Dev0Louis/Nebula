package dev.louis.nebula.api.mana;

@SuppressWarnings("UnusedReturnValue")
public interface ManaManager extends ManaPool {

    /**
     * Querys the ManaManager's state to be synced to the client.
     */
    void querySync();

    /**
     * Sends the ManaManager's state to the client.
     * @return If the state was successfully send.
     */
    boolean sendSync();
}
