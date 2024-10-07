package dev.louis.nebula.api.entrypoint;

import dev.louis.nebula.api.mana.source.factory.EntityManaSourceFactory;
import net.minecraft.util.Identifier;

public interface AlternativeManaSourceRegisterer {
    void registerPre(Identifier id, EntityManaSourceFactory factory);

    void registerPost(Identifier id, EntityManaSourceFactory factory);
}
