package dev.louis.nebula.api.entrypoint;

import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;

public interface EntityManaPoolEntrypoint {
    void registerEntityManaPool(EntityManaPoolRegistererImpl registerer);
}
