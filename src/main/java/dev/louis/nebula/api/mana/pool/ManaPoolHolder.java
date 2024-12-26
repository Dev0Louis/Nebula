package dev.louis.nebula.api.mana.pool;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface ManaPoolHolder {
    default @NotNull ManaPool getManaPool() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }
}
