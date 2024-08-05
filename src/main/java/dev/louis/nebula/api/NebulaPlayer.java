package dev.louis.nebula.api;

import dev.louis.nebula.api.mana.ManaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

/**
 * This interface will be injected into {@link PlayerEntity}.
 *
 */
public interface NebulaPlayer {
    default @NotNull ManaManager getManaManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    static ManaManager getManaManager(ServerPlayerEntity serverPlayer) {
        return serverPlayer.getManaManager();
    }
}
