package dev.louis.nebula.api.mana.pool.entity;

import dev.louis.nebula.api.mana.pool.ManaPool;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface EntityManaPoolFactory {
    ManaPool create(LivingEntity entity);
}
