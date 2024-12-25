package dev.louis.nebula.api.mana.manager;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface ManaManagerHolder {
    default @NotNull ManaManager getManaManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    static ClientManaManager getManaManager(ClientPlayerEntity entity) {
        return (ClientManaManager) entity.getManaManager();
    }

    static ServerManaManager getManaManager(ServerPlayerEntity entity) {
        return (ServerManaManager) entity.getManaManager();
    }

    static ManaManager getManaManager(LivingEntity entity) {
        return entity.getManaManager();
    }
}
