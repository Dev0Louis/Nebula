package dev.louis.nebulo.client;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebulo.Nebulo;
import dev.louis.nebulo.spell.CloudJumpSpell;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public interface SpellCreator {
    RegistryKey<Registry<SpellCreator>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Nebulo.MOD_ID, "spells"));
    SimpleRegistry<SpellCreator> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    SpellCreator CLOUD_CREATOR = Registry.register(REGISTRY, Identifier.of(Nebulo.MOD_ID, "cloud_spell"), (player) -> new CloudJumpSpell());

    static void init() {

    }

    Spell<PlayerEntity> create(PlayerEntity caster);
}
