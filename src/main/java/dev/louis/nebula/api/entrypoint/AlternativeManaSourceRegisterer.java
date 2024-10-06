package dev.louis.nebula.api.entrypoint;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.mana.factory.EntityManaSourceFactory;
import dev.louis.nebula.util.Phase;
import net.minecraft.util.Identifier;

public final class AlternativeManaSourceRegisterer {
    public AlternativeManaSourceRegisterer() {
    }

    public void registerPre(Identifier id, EntityManaSourceFactory factory) {
        Nebula.registerManaAlternativeInPhase(id, factory, Phase.PRE);
    }

    public void registerPost(Identifier id, EntityManaSourceFactory factory) {
        Nebula.registerManaAlternativeInPhase(id, factory, Phase.POST);
    }
}
