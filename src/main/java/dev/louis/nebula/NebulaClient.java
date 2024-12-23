package dev.louis.nebula;

import dev.louis.nebula.mana.NebulaManaManager;
import dev.louis.nebula.networking.s2c.play.StopSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.StartSpellEffectPayload;
import dev.louis.nebula.networking.s2c.play.SyncManaPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NebulaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    public void registerPacketReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SyncManaPayload.ID, NebulaManaManager::receiveMana);
        ClientPlayNetworking.registerGlobalReceiver(StartSpellEffectPayload.ID, NebulaClient::receiveStartSpellEffect);
        ClientPlayNetworking.registerGlobalReceiver(StopSpellEffectPayload.ID, NebulaClient::receiveStopSpellEffect);
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("resource")
    public static void receiveStartSpellEffect(StartSpellEffectPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> {
            var entity = context.client().world.getEntityById(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.startSpellEffect(payload.getSpellEffect());
            }
        });    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("resource")
    public static void receiveStopSpellEffect(StopSpellEffectPayload payload, ClientPlayNetworking.Context context) {
        context.client().executeSync(() -> {
            var entity = context.client().world.getEntityById(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.stopSpellEffect(payload.getSpellEffect());
            }
        });
    }

}
