package dev.louis.nebula.api.mana.source.factory;

import dev.louis.nebula.api.mana.source.ManaSource;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface EntityManaSourceFactory {
    ManaSource create(LivingEntity entity);
}
