package dev.louis.nebula.api.mana.pool;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ManaPoolHolder {
    default @NotNull ManaPool getManaPool() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    static ManaPool getManaPool(LivingEntity entity) {
        return entity.getManaPool();
    }
}
