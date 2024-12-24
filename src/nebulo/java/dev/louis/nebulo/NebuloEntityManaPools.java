package dev.louis.nebulo;

import dev.louis.nebula.api.entrypoint.EntityManaPoolEntrypoint;
import dev.louis.nebula.api.mana.pool.entity.EntityManaPoolType;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistererImpl;
import dev.louis.nebulo.mana.LapisManaSource;
import net.minecraft.util.Identifier;

public class NebuloEntityManaPools implements EntityManaPoolEntrypoint {

    @Override
    public void registerEntityManaPool(EntityManaPoolRegistererImpl registerer) {
        registerer.register(Identifier.of(Nebulo.MOD_ID, "lapis"), EntityManaPoolType.create(LapisManaSource::create));
    }
}
