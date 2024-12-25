package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.container.ManaContainer;
import dev.louis.nebula.constants.NbtConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;

import static dev.louis.nebula.constants.NbtConstants.MANA;

@ApiStatus.Experimental
public class SimpleManaContainer extends SnapshotParticipant<Float> implements ManaContainer {
    private final float capacity;
    private float mana;

    public SimpleManaContainer(int startingMana, float capacity) {
        this.mana = startingMana;
        this.capacity = capacity;
    }

    @Override
    public float getCapacity() {
        return capacity;
    }

    @Override
    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = Math.clamp(mana, 0, capacity);
    }

    @Override
    public float insertMana(float amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Insertion amount is negative.");
        float insertion = Math.min(amount, getCapacity());

        // implicit NaN check (as NaN > x = false)
        var shouldInsert = insertion > 0;

        if (shouldInsert) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
        }

        return insertion;
    }

    @Override
    public float extractMana(float amount, TransactionContext context) {
        if (amount < 0) throw new IllegalArgumentException("Extraction amount is negative.");
        float extraction = Math.min(amount, getCapacity());

        // implicit NaN check (as NaN > x = false)
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
        nebulaNbt.putFloat(MANA, this.getMana());
        nbt.put(NbtConstants.NEBULA, nebulaNbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(NbtConstants.NEBULA);
        this.setMana(nebulaNbt.getFloat(MANA));
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
