package dev.louis.nebulo.client;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebulo.NebuloSpells;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicInteger;

public class NebuloClient implements ClientModInitializer {
    public int spellCooldown = 0;
    @Override
    public void onInitializeClient() {
        registerKeybind();
        registerKeybindCallback();

        registerRenderCallback();
    }


    private static void registerKeybind() {
        var keyBind = new KeyBinding(
                "key.nebulo.example",
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.nebulo"
        );
        KeyBindingHelper.registerKeyBinding(keyBind);
        SpellKeybindManager.addSpellKeyBinding(NebuloSpells.CLOUD_JUMP, keyBind);
    }

    private void registerKeybindCallback() {
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

    private void registerRenderCallback() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            var player = MinecraftClient.getInstance().player;
            if(player == null)return;
            var manaManager = player.getManaManager();
            var spellManager = player.getSpellManager();
            var mana = String.valueOf(manaManager.getMana());
            var maxMana = String.valueOf(manaManager.getMaxMana());
            var textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(
                    matrixStack,
                    "Mana: " + mana + "/" + maxMana,
                    10,
                    10,
                    0x0000FF
            );

            textRenderer.drawWithShadow(
                    matrixStack,
                    "Learned Spells:",
                    10,
                    20,
                    0x00FFFF
            );

            var spells = spellManager.getLearnedSpells();
            AtomicInteger y = new AtomicInteger(30);
            spells.forEach(spellType -> {
                textRenderer.drawWithShadow(
                        matrixStack,
                        spellType.getId().toString(),
                        10,
                        y.get(),
                        0x03F6FF
                );
                y.addAndGet(10);
            });
        });
    }
}
