package dev.louis.nebula.api.spell;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public record SpellEffectType<T extends SpellEffect>(Identifier id, Factory<T> factory) {
    public static final RegistryKey<Registry<SpellEffectType<?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "spell_effect_type"));
    public static final SimpleRegistry<SpellEffectType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public interface Factory<T extends SpellEffect> {
        T create(LivingEntity entity);
    }
}
