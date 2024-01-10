package dev.louis.nebula.renderer;

import com.google.common.collect.ImmutableMap;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.util.Map;

public class SpellRenderers {
    private static final Map<SpellType<?>, SpellRendererFactory<?>> RENDERER_FACTORIES = new Object2ObjectOpenHashMap<>();

    public static <T extends Spell> void register(SpellType<? extends T> type, SpellRendererFactory<T> factory) {
        RENDERER_FACTORIES.put(type, factory);
    }

    public static Map<SpellType<?>, SpellRenderer<?>> reloadEntityRenderers(SpellRendererFactory.Context ctx) {
        ImmutableMap.Builder<SpellType<?>, SpellRenderer<?>> builder = ImmutableMap.builder();
        RENDERER_FACTORIES.forEach((spellType, factory) -> {
            try {
                builder.put(spellType, factory.create(ctx));
            } catch (Exception var5) {
                throw new IllegalArgumentException("Failed to create renderer for " + spellType.getId(), var5);
            }
        });
        return builder.build();
    }

    public static void registerRenderCallback() {
        WorldRenderEvents.END.register(context -> {

        });
    }

}
