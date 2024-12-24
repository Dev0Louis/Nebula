package dev.louis.nebula.api.entrypoint;

import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import net.minecraft.util.Identifier;

public interface ManaPoolRegisterer {
    void register(Identifier id, EntityManaPoolType factory);
}
