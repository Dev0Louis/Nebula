package dev.louis.nebula.api.spell.executer;

import com.mojang.serialization.MapCodec;
import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public record SpellExecutorType<T extends SpellExecutor>(MapCodec<SpellExecutorType<T>> mapCodec) {
    public static final RegistryKey<Registry<SpellExecutorType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "ticking_spell_type"));
    public static final Registry<SpellExecutorType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();



}
