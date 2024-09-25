package dev.louis.nebulo.client;

import dev.louis.nebula.api.spell.SpellEffectType;
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
        SpellKeybindManager.addSpellKeyBinding(keyBind, SpellCreator.CLOUD_CREATOR);
    }

    private void registerKeybindCallback() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (spellCooldown > 0) {
                spellCooldown--;
                return;
            }

            SpellKeybindManager.checkPresses();
        });
    }

    private void registerRenderCallback() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            var player = MinecraftClient.getInstance().player;
            if(player == null)return;
            var manaManager = player.getManaManager();
            var spellEffects = player.getSpellEffects();
            var mana = String.valueOf(manaManager.getMana());
            var maxMana = String.valueOf(manaManager.getCapacity());
            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "Mana: " + mana + "/" + maxMana,
                    10,
                    10,
                    0x0000FF,
                    false
            );

            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "Spell Effects:",
                    10,
                    20,
                    0x00FFFF,
                    true
            );


            AtomicInteger y = new AtomicInteger(30);
            spellEffects.forEach(spellEffect -> {
                drawContext.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        SpellEffectType.REGISTRY.getId(spellEffect.getType()).toString(),
                        10,
                        y.get(),
                        0x03F6FF,
                        true
                );
                y.addAndGet(10);
            });
        });
    }
}
