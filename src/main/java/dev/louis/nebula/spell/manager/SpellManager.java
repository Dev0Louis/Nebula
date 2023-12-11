package dev.louis.nebula.spell.manager;

import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import dev.louis.nebula.spell.TickingSpell;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public interface SpellManager {
    void tick();

    /**
     * This shouldn't be called directly, it is called by {@link TickingSpell#cast()}
     * @param tickingSpell The TickingSpell which should start ticking.
     * @return If the TickingSpell was successfully added.
     */
    boolean startTickingSpell(TickingSpell tickingSpell);

    /**
     * Stops the TickingSpell from ticking.
     * {@link TickingSpell#stop()} will be called when this is called.
     * @param tickingSpell The TickingSpell which should be stopped.
     * @return If the TickingSpell was successfully stopped.
     */
    boolean stopTickingSpell(TickingSpell tickingSpell);

    /**
     * Checks if a TickingSpell of the specified SpellType is currently ticking.
     * @param spellType The SpellType which should be checked.
     * @return If a TickingSpell with the specified SpellType is currently ticking.
     */
    boolean isSpellTypeTicking(SpellType<? extends TickingSpell> spellType);

    /**
     * Checks if a TickingSpell is currently ticking.
     * @param tickingSpell The TickingSpell which should be checked.
     * @return If the TickingSpell is currently ticking.
     */
    boolean isSpellTicking(TickingSpell tickingSpell);

    /**
     * Learns the specified SpellType.
     * @param spellType The SpellType which should be learned.
     * @return If the SpellType was successfully learned. Returns false if the SpellType is already learned or the learning failed.
     */
    boolean learnSpell(SpellType<?> spellType);

    /**
     * Forgets the specified SpellType.
     * @param spellType The SpellType which should be forgotten.
     * @return If the SpellType was successfully forgotten. Returns false if the SpellType was not learned or the forgetting failed.
     */
    boolean forgetSpell(SpellType<?> spellType);

    /**
     * Casts a Spell of the specified SpellType. The Spell may not cast if it is not castable, cancelled by {@link dev.louis.nebula.event.SpellCastCallback} or other reasons.
     * @param spellType The SpellType which should be cast.
     */
    void cast(SpellType<?> spellType);

    /**
     * Casts a Spell. The Spell may not cast if it is not castable, cancelled by {@link dev.louis.nebula.event.SpellCastCallback} or other reasons.
     * @param spell The Spell which should be casted.
     */
    void cast(Spell spell);

    /**
     * This is called when the Caster dies.
     * @param damageSource The DamageSource of the death.
     */
    void onDeath(DamageSource damageSource);

    /**
     * Checks if the specified SpellType is castable.
     * @param spellType The SpellType which should be checked.
     * @return If the SpellType is castable.
     */
    boolean isCastable(SpellType<?> spellType);

    /**
     * Checks if the specified has been learned.
     * @param spellType The SpellType which should be checked.
     * @return If the SpellType has been learned.
     */
    boolean hasLearned(SpellType<?> spellType);

    /**
     * Sends the SpellManager's state to the client.
     * @return If the state was successfully send.
     */
    boolean sendSync();

    /**
     * Receives the SpellManager's state for the client. This shall never be called by the server.
     * @return If the state was successfully received.
     */
    boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);

    /**
     * Writes the Nbt data of the SpellManager.
     * @param nbt The nbt data that shall be written to.
     * @return The nbt data that has been written to.
     */
    NbtCompound writeNbt(NbtCompound nbt);

    /**
     * Reads the Nbt data of the SpellManager.
     * @param nbt The nbt data that shall be read.
     */
    void readNbt(NbtCompound nbt);

    /**
     * Sets the PlayerEntity of the SpellManager.
     * @param player The new PlayerEntity of the SpellManager.
     * @return The SpellManager with the new PlayerEntity.
     */
    SpellManager setPlayer(PlayerEntity player);

    @FunctionalInterface
    interface Factory<T extends SpellManager> {
        T createSpellKnowledgeManager(PlayerEntity player);
    }

    /**
     * SpellManager indicating that no ManaManager has been constructed yet.
     * Is also used for Player classes that never construct a SpellManager like {@link ClientPlayerEntity}.
     * Calling methods is safe, but won't do anything.
     */
    SpellManager EMPTY = new SpellManager() {
        @Override
        public void tick() {

        }

        @Override
        public boolean startTickingSpell(TickingSpell tickingSpell) {
            return false;
        }

        @Override
        public boolean stopTickingSpell(TickingSpell tickingSpell) {
            return false;
        }

        @Override
        public boolean isSpellTypeTicking(SpellType<? extends TickingSpell> spellType) {
            return false;
        }

        @Override
        public boolean isSpellTicking(TickingSpell tickingSpell) {
            return false;
        }

        @Override
        public boolean learnSpell(SpellType<?> spellType) {
            return false;
        }

        @Override
        public boolean forgetSpell(SpellType<?> spellType) {
            return false;
        }

        @Override
        public void cast(SpellType<?> spellType) {

        }

        @Override
        public void cast(Spell spell) {

        }

        @Override
        public void onDeath(DamageSource damageSource) {

        }

        @Override
        public boolean isCastable(SpellType<?> spellType) {
            return false;
        }

        @Override
        public boolean hasLearned(SpellType<?> spellType) {
            return false;
        }

        @Override
        public boolean sendSync() {
            return false;
        }

        @Override
        public boolean receiveSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            return false;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            return null;
        }

        @Override
        public void readNbt(NbtCompound nbt) {

        }

        @Override
        public SpellManager setPlayer(PlayerEntity player) {
            return null;
        }
    };
}
