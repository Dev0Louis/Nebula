package dev.louis.nebula.api.spell;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record SpellEffectType<T extends SpellEffect>(Factory<T> factory) {
    public static final RegistryKey<Registry<SpellEffectType<?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "spell_effect_type"));
    public static final SimpleRegistry<SpellEffectType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();


    public static <T extends SpellEffect> SpellEffectType<T> register(Identifier id, SpellEffectType<T> spellEffectType) {
        return Registry.register(REGISTRY, id, spellEffectType);
    }

    public static <T extends SpellEffect> SpellEffectType<T> register(Identifier id, Factory<T> spellFactory) {
        return Registry.register(REGISTRY, id, new SpellEffectType<>(spellFactory));
    }

    public interface Factory<T extends SpellEffect> {
        T create(LivingEntity entity);
    }

    public static void init() {

    }
}
