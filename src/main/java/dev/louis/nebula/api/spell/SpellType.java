package dev.louis.nebula.api.spell;

import dev.louis.nebula.Nebula;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record SpellType<T extends Spell>(Identifier id, Factory<T> factory) {
    public static final RegistryKey<Registry<SpellType>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebula.MOD_ID, "spell_type"));
    public static final SimpleRegistry<SpellType> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public interface Factory<T extends Spell> {
        T create(World world);
    }
}
