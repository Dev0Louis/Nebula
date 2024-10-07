package dev.louis.nebulo;

import dev.louis.nebula.api.entrypoint.AlternativeManaSourceRegisteringEntrypoint;
import dev.louis.nebula.entrypoint.AlternativeManaSourceRegistererImpl;
import dev.louis.nebulo.mana.LapisManaSource;
import net.minecraft.util.Identifier;

public class NebuloAlternativeManaSources implements AlternativeManaSourceRegisteringEntrypoint {

    @Override
    public void registerAlternativeManaSources(AlternativeManaSourceRegistererImpl registerer) {
        registerer.registerPost(Identifier.of(Nebulo.MOD_ID, "lapis"), LapisManaSource::create);
    }
}
