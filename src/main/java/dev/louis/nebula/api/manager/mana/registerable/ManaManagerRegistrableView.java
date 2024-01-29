package dev.louis.nebula.api.manager.mana.registerable;

import dev.louis.nebula.api.manager.mana.ManaManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface ManaManagerRegistrableView {
        void registerManaManager(
                ManaManager.Factory<?> manaManagerFactory,
                Identifier manaPacketId,
                Consumer<Identifier> manaPacketRegisterer
        );
}