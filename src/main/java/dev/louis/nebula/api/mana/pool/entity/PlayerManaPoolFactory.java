package dev.louis.nebula.api.mana.pool.entity;

import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface PlayerManaPoolFactory {
    ManaAttachment create(ServerPlayerEntity entity);
}
