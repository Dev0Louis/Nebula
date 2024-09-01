package dev.louis.nebula;

import dev.louis.nebula.api.mana.ManaManagerHolder;
import dev.louis.nebula.mana.NebulaManaManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface InternalManaManagerHolder extends ManaManagerHolder {
    @Override
    default @NotNull NebulaManaManager getManaManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    static NebulaManaManager getManaManager(PlayerEntity player) {
        return ((InternalManaManagerHolder) player).getManaManager();
    }
}
