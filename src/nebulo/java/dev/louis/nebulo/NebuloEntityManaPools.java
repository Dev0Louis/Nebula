package dev.louis.nebulo;

import dev.louis.nebula.api.entrypoint.EntityManaPoolEntrypoint;
import dev.louis.nebula.api.entrypoint.EntityManaPoolRegistrar;
import dev.louis.nebula.entrypoint.EntityManaPoolRegistrarImpl;
import dev.louis.nebulo.mana.LapisManaSource;
import net.minecraft.util.Identifier;

public class NebuloEntityManaPools implements EntityManaPoolEntrypoint {

    @Override
    public void registerEntityManaPool(EntityManaPoolRegistrar registerer) {
        registerer.register(Identifier.of(Nebulo.MOD_ID, "lapis"), LapisManaSource.TYPE);
    }
}
