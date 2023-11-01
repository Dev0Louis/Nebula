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

import java.util.Set;

public interface SpellManager {
    void tick();

    boolean startTickingSpell(TickingSpell tickingSpell);


    boolean stopTickingSpell(TickingSpell tickingSpell);
    void setTickingSpells(Set<TickingSpell> tickingSpells);
    boolean isSpellTicking(SpellType<? extends Spell> spellType);
    boolean isSpellTicking(TickingSpell tickingSpell);

    boolean learnSpell(SpellType<? extends Spell> spellType);
    boolean forgetSpell(SpellType<? extends Spell> spellType);

    void cast(PlayerEntity player, SpellType<? extends Spell> spellType);

    void cast(Spell spell);

    void copyFrom(PlayerEntity oldNebulaPlayer, boolean alive);
    void onDeath(DamageSource damageSource);
    boolean isCastable(SpellType<? extends Spell> spellType);
    boolean hasLearned(SpellType<? extends Spell> spellType);
    boolean sendSync();
    boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
    NbtCompound writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);

    @FunctionalInterface
    interface Factory<T extends SpellManager> {
        T createSpellKnowledgeManager(PlayerEntity player);
    }
}
