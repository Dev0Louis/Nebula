
package dev.louis.nebulo.client;


import dev.louis.nebulo.networking.CastSpellPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;

/**
 The SpellKeybinds class stores a mapping between spell types and key bindings.
 It allows for easy retrieval of the key binding associated with a specific spell type.
 */
@Environment(EnvType.CLIENT)
public class SpellKeybindManager {

    // HashMap that stores the mapping between spell types and key bindings
    private static final HashMap<KeyBinding, SpellCreator> keyBindings = new HashMap<>();

    /**
     * Sets the key binding associated with a specific spell type.
     *
     * @param keyBinding the key binding to associate with the spell type
     * @param spellCreator the spell executor that is to be executed.
     */
    public static void addSpellKeyBinding(KeyBinding keyBinding, SpellCreator spellCreator) {
        keyBindings.put(keyBinding, spellCreator);
    }

    public static void checkPresses() {
        keyBindings.forEach((key, spellCreator) -> {
            if (key.isPressed()) {
                var player = MinecraftClient.getInstance().player;
                if (player != null) {
                    ClientPlayNetworking.send(new CastSpellPayload(SpellCreator.REGISTRY.getId(spellCreator)));

                    player.castSpell(spellCreator.create(player));
                }
            }
        });
    }
}

