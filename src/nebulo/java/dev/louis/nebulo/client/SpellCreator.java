package dev.louis.nebulo.client;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebulo.Nebulo;
import dev.louis.nebulo.spell.CloudJumpSpell;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface SpellCreator {

    static void init() {

    }

    Spell<ServerPlayerEntity> cast(ServerPlayerEntity caster);
}
