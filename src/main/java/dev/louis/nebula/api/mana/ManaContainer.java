package dev.louis.nebula.api.mana;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class ManaContainer extends SnapshotParticipant<Integer> implements ManaHolder {
    private final int capacity;
    private int mana;

    public ManaContainer(int startingMana, int capacity) {
        this.mana = startingMana;
        this.capacity = capacity;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int mana() {
        return mana;
    }

    @Override
    public void setMana(int mana) {
        if (mana > capacity) {
            Nebula.LOGGER.warn("A ManaContainer with capacity of " + capacity + " was set to hold " + mana + " mana. Clamping the Value.");
            mana = capacity;
        }

        this.mana = mana;
    }

    @Override
    public int insert(int amount, TransactionContext context) {
        int insertion = Math.min(amount, capacity - mana);

        if (insertion > 0) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
            return insertion;
        }

        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext context) {
        int extraction = Math.min(amount, capacity - mana);

        if (extraction > 0) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
            return extraction;
        }

        return 0;
    }

    @Override
    protected Integer createSnapshot() {
        return mana;
    }

    @Override
    protected void readSnapshot(Integer snapshot) {
        this.mana = snapshot;
    }
}
