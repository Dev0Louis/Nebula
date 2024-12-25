package dev.louis.nebula.api.mana.container;

import dev.louis.nebula.api.mana.pool.ManaPool;
import dev.louis.nebula.mana.SimpleManaContainer;
import net.minecraft.nbt.NbtCompound;

public interface ManaContainer extends ManaPool {

    static ManaContainer createSimple(int baseMana, int maxMana) {
        return new SimpleManaContainer(baseMana, maxMana);
    }
}
