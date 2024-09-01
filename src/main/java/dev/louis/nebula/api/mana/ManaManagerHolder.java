package dev.louis.nebula.api.mana;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ManaManagerHolder {
    default @NotNull ManaManager getManaManager() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }

    static ManaManager getManaManager(LivingEntity entity) {
        return entity.getManaManager();
    }
}
