package dev.louis.nebula.mana;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.ManaPool;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;

import static dev.louis.nebula.Nebula.MANA_NBT_KEY;

@ApiStatus.Experimental
public class SimpleManaContainer extends SnapshotParticipant<Float> implements ManaPool {
    private final float capacity;
    private float mana;

    public SimpleManaContainer(int startingMana, float capacity) {
        this.mana = startingMana;
        this.capacity = capacity;
    }

    @Override
    public float capacity() {
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
        float insertion = Math.min(amount, capacity - mana);

        if (insertion > 0) {
            updateSnapshots(context);
            this.mana = this.mana + insertion;
            return insertion;
        }

        return 0;
    }

    @Override
    public float extractMana(float amount, TransactionContext context) {
        float extraction = Math.min(amount, capacity - mana);

        if (extraction > 0) {
            updateSnapshots(context);
            this.mana = this.mana - extraction;
            return extraction;
        }

        return 0;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        nebulaNbt.putFloat(MANA_NBT_KEY, this.getMana());
        nbt.put(Nebula.MOD_ID, nebulaNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound nebulaNbt = nbt.getCompound(Nebula.MOD_ID);
        this.setMana(nebulaNbt.getFloat(MANA_NBT_KEY));
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
