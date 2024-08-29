package dev.louis.nebula.api.mana;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class ManaContainer extends SnapshotParticipant<Float> implements ManaHolder {
    private final int capacity;
    private float mana;

    public ManaContainer(int startingMana, int capacity) {
        this.mana = startingMana;
        this.capacity = capacity;
    }

    @Override
    public int manaCapacity() {
        return capacity;
    }

    @Override
    public float getMana() {
        return mana;
    }

    @Override
    public void setMana(float mana) {
        if (mana > capacity) {
            Nebula.LOGGER.warn("A ManaContainer with capacity of " + capacity + " was set to hold " + mana + " mana. Clamping the Value.");
            mana = capacity;
        }

        this.mana = mana;
    }

    @Override
    public float insert(float amount, TransactionContext context) {
        float insertion = Math.min(amount, capacity - mana);

        if (insertion > 0) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
            return insertion;
        }

        return 0;
    }

    @Override
    public float extract(float amount, TransactionContext context) {
        float extraction = Math.min(amount, capacity - mana);

        if (extraction > 0) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
            return extraction;
        }

        return 0;
    }

    @Override
    protected Float createSnapshot() {
        return mana;
    }

    @Override
    protected void readSnapshot(Float snapshot) {
        this.mana = snapshot;
    }
}
