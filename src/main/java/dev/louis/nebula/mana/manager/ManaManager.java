package dev.louis.nebula.mana.manager;

import dev.louis.nebula.api.NebulaUser;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public interface ManaManager {
    void tick();
    void setMana(int mana);
    int getMaxmana();
    int getMana();
    void addMana(int mana);
    void drainMana(int mana);
    int getMaxMana();
    boolean sendSync();
    boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    void writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);
    void copyFrom(NebulaUser oldPlayer, boolean alive);
    @FunctionalInterface
    interface Factory<T extends ManaManager> {
        T createPlayerManaManager(NebulaUser player);
    }
}
