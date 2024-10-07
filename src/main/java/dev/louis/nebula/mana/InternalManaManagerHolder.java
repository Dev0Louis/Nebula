package dev.louis.nebula.mana;

import dev.louis.nebula.api.mana.manager.ManaManagerHolder;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface InternalManaManagerHolder extends ManaManagerHolder {
    @Override
    default @NotNull NebulaManaManager getManaManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    static NebulaManaManager getManaManager(LivingEntity entity) {
        return ((InternalManaManagerHolder) entity).getManaManager();
    }
}
