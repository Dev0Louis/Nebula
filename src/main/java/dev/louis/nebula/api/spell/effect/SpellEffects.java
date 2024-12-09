package dev.louis.nebula.api.spell.effect;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SpellEffects {
    public static final RegistryKey<Registry<SpellEffect>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "spell_effect"));
    public static final SimpleRegistry<SpellEffect> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();


    private SpellEffects() {

    }


    public static <T extends SpellEffect> T register(Identifier id, Factory<T> factory) {
        return Registry.register(
                REGISTRY,
                id,
                factory.create(id)
        );
    }

    public interface Factory<T extends SpellEffect> {
        T create(Identifier id);
    }

    public static void init() {

    }
}
