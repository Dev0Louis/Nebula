package dev.louis.nebula.api.mana.pool.entity;

import com.mojang.serialization.MapCodec;
import dev.louis.nebula.api.mana.pool.ManaPool;

public interface EntityManaPool extends ManaPool {
    EntityManaPoolType getType();
    void tick();
}
