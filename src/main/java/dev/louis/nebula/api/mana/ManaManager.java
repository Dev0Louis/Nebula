package dev.louis.nebula.api.mana;

import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface ManaManager {

    /**
     * @return The current amount of available mana.
     */
    int getMana();

    /**
     * Adds the specified amount of mana.
     * @param mana The amount of mana to add.
     */
    void addMana(int mana, TransactionContext context);

    /**
     * Drains the specified amount of mana.
     * @param mana The amount of mana to drain.
     */
    void drainMana(int mana, TransactionContext context);

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
}
