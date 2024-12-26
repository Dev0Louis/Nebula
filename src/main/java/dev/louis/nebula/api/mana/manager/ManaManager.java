package dev.louis.nebula.api.mana.manager;

import dev.louis.nebula.api.mana.storage.ManaStorage;

public interface ManaManager extends ManaStorage {
    float getMana();
    float getCapacity();
    void tick();
}
