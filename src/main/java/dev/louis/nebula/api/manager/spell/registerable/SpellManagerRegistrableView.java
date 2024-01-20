package dev.louis.nebula.api.manager.spell.registerable;

import dev.louis.nebula.api.manager.spell.SpellManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public interface SpellManagerRegistrableView {
        void registerSpellManagerFactory(
                SpellManager.Factory<?> manaManagerFactory,
                Identifier packetId,
                ClientPlayNetworking.PlayChannelHandler playChannelHandler
        );
}