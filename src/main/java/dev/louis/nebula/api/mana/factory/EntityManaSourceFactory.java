package dev.louis.nebula.api.mana.factory;

import dev.louis.nebula.api.mana.ManaSource;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface EntityManaSourceFactory {
    ManaSource create(LivingEntity entity);
}
