package dev.louis.nebulo.spell;

import dev.louis.nebulo.Nebulo;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class NebuloSpells {

    RegistryKey<Registry<NebuloSpells>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebulo.MOD_ID, "spells"));
    SimpleRegistry<NebuloSpells> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    CloudJumpSpell CLOUD_SPELL = Registry.register(REGISTRY, Identifier.of(Nebulo.MOD_ID, "cloud_spell"), new CloudJumpSpell());
}
