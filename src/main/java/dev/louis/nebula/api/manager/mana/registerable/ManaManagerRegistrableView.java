package dev.louis.nebula.api.manager.mana.registerable;

import dev.louis.nebula.api.manager.mana.ManaManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public interface ManaManagerRegistrableView {
        void registerManaManagerFactory(
                ManaManager.Factory<?> manaManagerFactory,
                Identifier packetId,
                ClientPlayNetworking.PlayChannelHandler playChannelHandler
        );
}