package dev.louis.nebula.mana.manager;

import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public interface ManaManager {
    void tick();

    void setMana(int mana);

    int getMana();

    void addMana(int mana);

    void drainMana(int mana);

    void drainMana(SpellType<?> spellType);

    int getMaxMana();

    boolean sendSync();

    boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);

    void writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    void onDeath(DamageSource damageSource);

    ManaManager setPlayer(PlayerEntity player);

    @FunctionalInterface
    interface Factory<T extends ManaManager> {
        T createPlayerManaManager(PlayerEntity player);
    }
}
