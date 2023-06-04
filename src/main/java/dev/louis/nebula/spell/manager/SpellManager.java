package dev.louis.nebula.spell.manager;

import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public interface SpellManager {
    void tick();

    boolean addSpell(SpellType<? extends Spell> spellType);
    boolean removeSpell(SpellType<? extends Spell> spellType);
    void copyFrom(ServerPlayerEntity oldPlayer, boolean alive);
    boolean canCast(SpellType<? extends Spell> spellType);
    boolean sendSync();
    boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    NbtCompound writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);

    @FunctionalInterface
    interface Factory<T extends SpellManager> {
        T createPlayerSpellKnowledgeManager(PlayerEntity player);
    }
}
