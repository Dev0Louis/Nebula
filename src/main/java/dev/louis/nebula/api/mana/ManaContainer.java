package dev.louis.nebula.api.mana;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class ManaContainer extends SnapshotParticipant<Integer> implements ManaHolder {
    private final int capacity = 20;
    private int mana;

    @Override
    public int mana() {
        return mana;
    }

    @Override
    public void setMana(int mana) {
        if (mana > capacity) throw new IllegalStateException("Can't overset the mana");
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
    protected Integer createSnapshot() {
        return mana;
    }

    @Override
    protected void readSnapshot(Integer snapshot) {
        this.mana = snapshot;
    }
}
