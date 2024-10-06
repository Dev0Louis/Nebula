package dev.louis.nebula.api;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.factory.EntityManaSourceFactory;
import dev.louis.nebula.util.Phase;
import net.minecraft.util.Identifier;

public class NebulaApi {

    public static void registerPreManaAlternative(Identifier id, EntityManaSourceFactory factory) {
        Nebula.registerManaAlternativeInPhase(id, factory, Phase.PRE);
    }

    public static void registerPostManaAlternative(Identifier id, EntityManaSourceFactory factory) {
        Nebula.registerManaAlternativeInPhase(id, factory, Phase.POST);
    }
}
