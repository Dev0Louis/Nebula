
package dev.louis.nebulo.client;


import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

/**
 The SpellKeybinds class stores a mapping between spell types and key bindings.
 It allows for easy retrieval of the key binding associated with a specific spell type.
 */
@Environment(EnvType.CLIENT)
public class SpellKeybindManager {

    // HashMap that stores the mapping between spell types and key bindings
    private static final HashMap<SpellType<? extends Spell>, KeyBinding> keyBindings = new HashMap<>();

    /**
     * Sets the key binding associated with a specific spell type.
     *
     * @param spellType the spell type to associate with the key binding
     * @param keyBinding the key binding to associate with the spell type
     */
    public static void addSpellKeyBinding(SpellType<? extends Spell> spellType, KeyBinding keyBinding) {
        keyBindings.put(spellType, keyBinding);
    }

    /**
     * Returns an Optional containing the key binding associated with a specific spell type.
     * If there is no key binding associated with the spell type, an empty Optional is returned.
     *
     * @param spellType the spell type to retrieve the key binding for
     * @return an Optional containing the key binding associated with the spell type, or an empty Optional
     */
    public static Optional<KeyBinding> getKey(@NotNull SpellType<? extends Spell> spellType){
        KeyBinding keyBinding = keyBindings.get(spellType);
        if(keyBinding==null)return Optional.empty();
        return Optional.of(keyBinding);
    }
}

