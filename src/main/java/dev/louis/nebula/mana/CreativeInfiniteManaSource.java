package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.pool.entity.EntityManaPool;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CreativeInfiniteManaSource implements EntityManaPool {
    public static final EntityManaPoolType TYPE = EntityManaPoolType.create(CreativeInfiniteManaSource::new);
    public LivingEntity entity;

    public CreativeInfiniteManaSource(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public long getMana() {
        return entity.isInCreativeMode() ? Long.MAX_VALUE : 0;
    }

    @Override
    public long getCapacity() {
        return entity.isInCreativeMode() ? Long.MAX_VALUE : 0;
    }

    @Override
    public long insertMana(long insertion, TransactionContext context) {
        return 0;
    }

    @Override
    public long extractMana(long extraction, TransactionContext context) {
        return entity.isInCreativeMode() ? extraction : 0;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

    @Override
    public EntityManaPoolType getType() {
        return TYPE;
    }

    @Override
    public void tick() {

    }
}
