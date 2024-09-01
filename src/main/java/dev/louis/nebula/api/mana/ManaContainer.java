package dev.louis.nebula.api.mana;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class ManaContainer extends SnapshotParticipant<Float> implements ManaPool {
    private final float capacity;
    private float mana;

    public ManaContainer(int startingMana, float capacity) {
        this.mana = startingMana;
        this.capacity = capacity;
    }

    @Override
    public float capacity() {
        return capacity;
    }

    @Override
    public float get() {
        return mana;
    }

    @Override
    public void set(float mana) {
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
