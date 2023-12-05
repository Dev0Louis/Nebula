package dev.louis.nebula.spell.manager;

import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import dev.louis.nebula.spell.TickingSpell;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public interface SpellManager {
    void tick();

    boolean startTickingSpell(TickingSpell tickingSpell);

    boolean stopTickingSpell(TickingSpell tickingSpell);

    boolean isSpellTypeTicking(SpellType<? extends TickingSpell> spellType);

    boolean isSpellTicking(TickingSpell tickingSpell);

    boolean learnSpell(SpellType<?> spellType);

    boolean forgetSpell(SpellType<?> spellType);

    void cast(SpellType<?> spellType);

    void cast(Spell spell);

    void onDeath(DamageSource damageSource);

    boolean isCastable(SpellType<?> spellType);

    boolean hasLearned(SpellType<?> spellType);

    boolean sendSync();

    boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    SpellManager setPlayer(PlayerEntity player);

    @FunctionalInterface
    interface Factory<T extends SpellManager> {
        T createSpellKnowledgeManager(PlayerEntity player);
    }
}
