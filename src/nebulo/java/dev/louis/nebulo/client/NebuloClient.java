package dev.louis.nebulo.client;

import dev.louis.nebula.api.mana.helper.ManaHelper;
import dev.louis.nebula.api.spell.effect.SpellEffect;
import dev.louis.nebulo.NebuloBlockEntities;
import dev.louis.nebulo.client.block.entity.renderer.ManaExtractorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class NebuloClient implements ClientModInitializer {
    private Collection<SpellEffect> serverSpellEffects = Collections.emptyList();
    private long serverThaum;
    private long serverCapacity;

    @Override
    public void onInitializeClient() {
        registerKeybind();
        registerKeybindCallback();
        registerServerTrackingCallback();
        registerRenderCallback();
        BlockEntityRendererFactories.register(NebuloBlockEntities.MANA_EXTRACTOR, ManaExtractorBlockEntityRenderer::new);
    }



    private static void registerKeybind() {
        var keyBind = new KeyBinding(
                "key.nebulo.cloud_jump",
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.nebulo"
        );
        KeyBindingHelper.registerKeyBinding(keyBind);
        SpellKeybindManager.addSpellKeyBinding(keyBind, SpellCreator.CLOUD_CREATOR);
    }

    private void registerServerTrackingCallback() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            var cplayer = MinecraftClient.getInstance().player;
            if (cplayer == null) return;
            var player = server.getPlayerManager().getPlayer(cplayer.getUuid());
            if (player == null) return;
            this.serverThaum = player.getManaManager().getThaum();
            this.serverCapacity = player.getManaManager().getThaumCapacity();
            this.serverSpellEffects = player.getSpellEffects();
        });
    }

    private void registerKeybindCallback() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            SpellKeybindManager.tickCooldown();
            SpellKeybindManager.checkPresses();
        });
    }

    private static String formatLong(long mana) {
        return ManaHelper.formatKilothaum(mana);
    }


    private void registerRenderCallback() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            var player = MinecraftClient.getInstance().player;
            if (player == null) return;
            var manaManager = player.getManaManager();
            var spellEffects = player.getSpellEffects();
            var thaum = formatLong(manaManager.getThaum());
            var maxThaum = formatLong(manaManager.getThaumCapacity());
            AtomicInteger x = new AtomicInteger(10);
            AtomicInteger y = new AtomicInteger(10);

            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "Thaum: " + thaum + "/" + maxThaum,
                    x.get(),
                    y.getAndAdd(10),
                    0x00c0FF,
                    false
            );

            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "Spell Effects:",
                    x.get(),
                    y.getAndAdd(10),
                    0x00FFFF,
                    true
            );


            spellEffects.forEach(spellEffect -> {
                drawContext.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        spellEffect.getId().toString(),
                        x.get(),
                        y.getAndAdd(10),
                        0x03F6FF,
                        true
                );
            });
            if (!MinecraftClient.getInstance().isConnectedToLocalServer()) return;
            x.addAndGet(200);
            y.set(10);

            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "(Server) Mana: " + formatLong(serverThaum) + "/" + formatLong(serverCapacity) + " Kilomana",
                    x.get(),
                    y.getAndAdd(10),
                    0xc000FF,
                    false
            );

            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "(Server) Spell Effects:",
                    x.get(),
                    y.getAndAdd(10),
                    0x60FFFF,
                    true
            );

            serverSpellEffects.forEach(spellEffect -> {
                drawContext.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        spellEffect.getId().toString(),
                        x.get(),
                        y.getAndAdd(10),
                        0x03F6FF,
                        true
                );
            });
        });
    }
}
