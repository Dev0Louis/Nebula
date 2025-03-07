package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.constants.NbtConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;

import static dev.louis.nebula.constants.NbtConstants.THAUM;

public class SimpleManaPool extends SnapshotParticipant<Long> implements ManaPool {
    private final long capacity;
    private long thaum;

    public SimpleManaPool(long startingMana, long capacity) {
        this.thaum = startingMana;
        this.capacity = capacity;
    }

    @Override
    public long getThaumCapacity() {
        return capacity;
    }

    @Override
    public long getThaum() {
        return thaum;
    }

    public void setThaum(long thaum) {
        this.thaum = Math.clamp(thaum, 0, capacity);
    }

    @Override
    public long insertThaum(long amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        long insertion = Math.min(amount, getThaumCapacity());

        var shouldInsert = insertion > 0;

        if (shouldInsert) {
            updateSnapshots(context);
            this.thaum = this.thaum + insertion;
        }

        return insertion;
    }

    @Override
    public long extractThaum(long amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        long extraction = Math.min(amount, getThaumCapacity());

        var shouldExtract = extraction > 0;

        if (shouldExtract) {
            updateSnapshots(context);
            this.thaum = this.thaum - extraction;
        }

        return extraction;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        nebulaNbt.putLong(THAUM, this.getThaum());
        nbt.put(NbtConstants.NEBULA, nebulaNbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        this.setThaum(nebulaNbt.getLong(THAUM));
    }

    @Override
    protected Long createSnapshot() {
        return thaum;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.thaum = snapshot;
    }
}
