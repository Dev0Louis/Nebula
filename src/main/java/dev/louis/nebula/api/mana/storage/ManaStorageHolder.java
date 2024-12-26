package dev.louis.nebula.api.mana.storage;

import dev.louis.nebula.api.mana.pool.ManaPool;
import org.jetbrains.annotations.NotNull;

public interface ManaStorageHolder {
    default @NotNull ManaStorage getManaStorage() {
        throw new UnsupportedOperationException("Injected Interface method was not overridden!");
    }


}
