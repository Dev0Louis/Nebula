package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.api.mana.source.ManaSource;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CreativeInfiniteManaSource implements ManaPool {
    public LivingEntity entity;

    public CreativeInfiniteManaSource(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public float getMana() {
        return Float.MAX_VALUE;
    }

    @Override
    public float insertMana(ServerWorld world, float insertion, TransactionContext context) {
        return 0;
    }

    @Override
    public float extractMana(ServerWorld world, float extraction, TransactionContext context) {
        return entity.isInCreativeMode() ? extraction : 0;
    }
}
