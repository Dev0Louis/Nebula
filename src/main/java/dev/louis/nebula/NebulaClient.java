package dev.louis.nebula;

import dev.louis.nebula.mana.NebulaManaManager;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    public void registerPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SyncManaPayload.ID, NebulaManaManager::receive);
        ClientPlayNetworking.registerGlobalReceiver(StartSpellEffectPayload.ID, NebulaManaManager::receive);
        ClientPlayNetworking.registerGlobalReceiver(StopSpellEffectPayload.ID, NebulaManaManager::receive);
    }
}
