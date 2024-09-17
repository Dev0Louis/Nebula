package dev.louis.nebula.cca;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.cca.component.SpellsComponent;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public class NebulaCCA implements ChunkComponentInitializer {
    public static final ComponentKey<SpellsComponent> SPELLS =
            ComponentRegistry.getOrCreate(Identifier.of(Nebula.MOD_ID, "spells"), SpellsComponent.class);

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(SPELLS, SpellsComponent::new);
    }
}
