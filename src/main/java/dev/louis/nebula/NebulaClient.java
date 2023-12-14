package dev.louis.nebula;

import dev.louis.nebula.networking.SynchronizeManaAmountS2CPacket;
import dev.louis.nebula.networking.UpdateSpellCastabilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NebulaClient implements ClientModInitializer {
    public static final Consumer<PacketByteBuf> retainer = getConsumer("retain", () -> PacketByteBuf::retain);
    public static final Consumer<PacketByteBuf> releaser = getConsumer("release", () -> PacketByteBuf::release);

    @Override
    public void onInitializeClient() {
        registerPacketReceivers();
    }

    private void registerPacketReceivers() {
        //Register the Spell Sync Packet.
        registerReceiver(UpdateSpellCastabilityS2CPacket.getID(),UpdateSpellCastabilityS2CPacket::receive);

        //Register the ManaAmount Packet.
        registerReceiver(SynchronizeManaAmountS2CPacket.getId(), SynchronizeManaAmountS2CPacket::receive);
    }

    public static void runWithBuf(MinecraftClient client, PacketByteBuf buf, Runnable runnable) {
        retainer.accept(buf);
        client.executeSync(() -> {
            runnable.run();
            releaser.accept(buf);
        });
    }

    private void registerReceiver(Identifier id, ClientPlayNetworking.PlayChannelHandler playChannelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(id, playChannelHandler);
    }

    private static Consumer<PacketByteBuf> getConsumer(String methodName, Supplier<Consumer<PacketByteBuf>> defaultConsumer) {
        try {
            //Needed to throw NoSuchMethodException if the method doesn't exist.
            PacketByteBuf.class.getMethod(methodName);
            return buf -> invoke(buf, methodName);
        } catch (NoSuchMethodException e) {
            return defaultConsumer.get();
        }
    }

    private static void invoke(PacketByteBuf buf, String methodName) {
        try {
            PacketByteBuf.class.getMethod(methodName).invoke(buf);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
