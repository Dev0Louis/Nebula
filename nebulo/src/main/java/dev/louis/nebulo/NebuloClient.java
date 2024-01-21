package dev.louis.nebulo;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebulo.client.SpellKeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class NebuloClient implements ClientModInitializer {
    public int spellCooldown = 0;
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (spellCooldown > 0) {
                spellCooldown--;
                return;
            }

            for (SpellType<?> spellType : SpellType.REGISTRY) {
                var optionalKey = SpellKeybindManager.getKey(spellType);
                if(optionalKey.isPresent()) {
                    var key = optionalKey.get();
                    if(key.isPressed()) {
                        client.player.getSpellManager().cast(spellType);
                        spellCooldown = 10;
                        return;
                    }
                }
            }
        });
    }
}
