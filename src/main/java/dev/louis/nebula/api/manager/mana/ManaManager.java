package dev.louis.nebula.api.manager.mana;

import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface ManaManager {
    /**
     * This is called every tick the player is ticked.
     * This should not be called manually.
     */
    void tick();

    /**
     * Sets the amount of mana.
     * @param mana The amount of mana to set.
     */
    void setMana(int mana);

    /**
     * @return The current amount of mana.
     */
    int getMana();

    /**
     * Adds the specified amount of mana.
     * @param mana The amount of mana to add.
     */
    void addMana(int mana);

    /**
     * Drains the specified amount of mana.
     * @param mana The amount of mana to drain.
     */
    void drainMana(int mana);

    /**
     * @return The maximum amount of mana that can be stored.
     */
    int getMaxMana();

    /**
     * @param mana The man amount that should be checked.
     * @return If enough mana is available.
     */
    boolean hasEnoughMana(int mana);

    //Thinking about removal as it would isolate the ManaManager from anything Spell related :)
    @Deprecated
    /**
     * @param spellType The SpellType which should be checked.
     * @return If enough mana is available for the specified SpellType.
     */
    boolean hasEnoughMana(SpellType<?> spellType);

    /**
     * Sends the ManaManager's state to the client.
     * @return If the state was successfully send.
     */
    boolean sendSync();

    /**
     * Writes the Nbt data of the SpellManager.
     * @param nbt The Nbt data that shall be written to.
     * @return The Nbt data that has been written to.
     */
    void writeNbt(NbtCompound nbt);

    /**
     * Reads the Nbt data of the ManaManager.
     * @param nbt The Nbt data that shall be read.
     */
    void readNbt(NbtCompound nbt);

    /**
     * This is called when the Player dies.
     * @param damageSource The DamageSource of the death.
     */
    void onDeath(DamageSource damageSource);

    /**
     * Sets the PlayerEntity of the ManaManager.
     * @param player The new PlayerEntity of the ManaManager.
     * @return The ManaManager with the new PlayerEntity.
     */
    ManaManager setPlayer(PlayerEntity player);

    default boolean isEmpty() {
        return false;
    }

    @FunctionalInterface
    interface Factory<T extends ManaManager> {
        T createPlayerManaManager(PlayerEntity player);
    }

    /**
     * ManaManager indicating that no ManaManager has been constructed yet.
     * Is also used for Player classes that never construct a ManaManager like {@link ClientPlayerEntity}.
     * Calling methods is safe, but won't do anything.
     */
    ManaManager EMPTY = new ManaManager() {

        @Override
        public void tick() {

        }

        @Override
        public void setMana(int mana) {

        }

        @Override
        public int getMana() {
            return 0;
        }

        @Override
        public void addMana(int mana) {

        }

        @Override
        public void drainMana(int mana) {

        }

        @Override
        public int getMaxMana() {
            return 0;
        }

        @Override
        public boolean hasEnoughMana(int mana) {
            return false;
        }

        @Override
        public boolean hasEnoughMana(SpellType<?> spellType) {
            return false;
        }

        @Override
        public boolean sendSync() {
            return false;
        }

        @Override
        public void writeNbt(NbtCompound nbt) {

        }

        @Override
        public void readNbt(NbtCompound nbt) {

        }

        @Override
        public void onDeath(DamageSource damageSource) {

        }

        @Override
        public ManaManager setPlayer(PlayerEntity player) {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };
}
