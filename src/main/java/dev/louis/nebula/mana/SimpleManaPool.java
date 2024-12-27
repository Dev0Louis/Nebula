package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.constants.NbtConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;

import static dev.louis.nebula.constants.NbtConstants.MANA;

@ApiStatus.Experimental
public class SimpleManaPool extends SnapshotParticipant<Long> implements ManaPool {
    private final long capacity;
    private long mana;

    public SimpleManaPool(long startingMana, long capacity) {
        this.mana = startingMana;
        this.capacity = capacity;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getMana() {
        return mana;
    }

    public void setMana(long mana) {
        this.mana = Math.clamp(mana, 0, capacity);
    }

    @Override
    public long insertMana(long amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        long insertion = Math.min(amount, getCapacity());

        var shouldInsert = insertion > 0;

        if (shouldInsert) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
        }

        return insertion;
    }

    @Override
    public long extractMana(long amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        long extraction = Math.min(amount, getCapacity());

        var shouldExtract = extraction > 0;

        if (shouldExtract) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
        }

        return extraction;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        nebulaNbt.putLong(MANA, this.getMana());
        nbt.put(NbtConstants.NEBULA, nebulaNbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        this.setMana(nebulaNbt.getLong(MANA));
    }

    @Override
    protected Long createSnapshot() {
        return mana;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.mana = snapshot;
    }
}
