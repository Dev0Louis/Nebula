package dev.louis.nebula.api.manager.spell;

import dev.louis.nebula.api.event.SpellCastCallback;
import dev.louis.nebula.api.networking.UpdateSpellCastabilityS2CPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface SpellManager {
    void tick();

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
     * Casts a Spell of the specified SpellType. The Spell may not cast if it is not castable, cancelled by {@link SpellCastCallback} or other reasons.
     * @param spellType The SpellType which should be cast.
     */
    void cast(SpellType<?> spellType);

    /**
     * Casts a Spell. The Spell may not cast if it is not castable, cancelled by {@link SpellCastCallback} or other reasons.
     * @param spell The Spell which should be cast.
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
     * Checks if the specified SpellType is currently active.
     * @param spellType The SpellType which should be checked.
     * @return If the SpellType is currently active.
     */
    boolean isSpellTypeActive(SpellType<?> spellType);

    /**
     * Checks if the specified Spell is currently active.
     * @param spell The Spell which should be checked.
     * @return If the Spell is currently active.
     */
    boolean isSpellActive(Spell spell);

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
    boolean receiveSync(UpdateSpellCastabilityS2CPacket packet);

    /**
     * Writes the Nbt data of the SpellManager.
     * @param nbt The nbt data that shall be written to.
     * @return The nbt data that has been written to.
     */
    void writeNbt(NbtCompound nbt);

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

    default boolean isEmpty() {
        return false;
    }

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
        public boolean isSpellTypeActive(SpellType<?> spellType) {
            return false;
        };

        @Override
        public boolean isSpellActive(Spell spell) {
            return false;
        };

        @Override
        public boolean hasLearned(SpellType<?> spellType) {
            return false;
        }

        @Override
        public boolean sendSync() {
            return false;
        }

        @Override
        public boolean receiveSync(UpdateSpellCastabilityS2CPacket packet) {
            return false;
        }

        @Override
        public void writeNbt(NbtCompound nbt) {

        }

        @Override
        public void readNbt(NbtCompound nbt) {

        }

        @Override
        public SpellManager setPlayer(PlayerEntity player) {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };
}
