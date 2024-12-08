
package dev.louis.nebulo.client;


import dev.louis.nebulo.networking.CastSpellPayload;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
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
    private static final Object2IntArrayMap<SpellCreator> COOLDOWN = new Object2IntArrayMap<>();

    /**
     * Sets the key binding associated with a specific spell type.
     *
     * @param keyBinding the key binding to associate with the spell type
     * @param spellCreator the spell executor that is to be executed.
     */
    public static void addSpellKeyBinding(KeyBinding keyBinding, SpellCreator spellCreator) {
        keyBindings.put(keyBinding, spellCreator);
    }

    public static void tickCooldown() {
        keyBindings.values().forEach(spellCreator -> {
            COOLDOWN.computeIfPresent(spellCreator, ((spellCreator1, aInt) -> {
                int bInt = aInt - 1;
                return bInt == 0 ? null : bInt;
            }));

        });
    }

    public static void checkPresses() {
        keyBindings.forEach((key, spellCreator) -> {
            if (key.isPressed() && COOLDOWN.getInt(spellCreator) == 0) {
                var player = MinecraftClient.getInstance().player;
                if (player != null) {
                    ClientPlayNetworking.send(new CastSpellPayload(SpellCreator.REGISTRY.getId(spellCreator)));
                    COOLDOWN.put(spellCreator, 20);
                }
            }
        });
    }
}

