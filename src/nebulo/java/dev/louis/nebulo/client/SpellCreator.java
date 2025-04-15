package dev.louis.nebulo.client;

import net.minecraft.server.network.ServerPlayerEntity;

public interface SpellCreator {

    static void init() {

    }

    Spell<ServerPlayerEntity> cast(ServerPlayerEntity caster);
}
