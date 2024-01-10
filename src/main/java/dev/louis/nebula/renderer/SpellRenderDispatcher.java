package dev.louis.nebula.renderer;

import com.google.common.collect.ImmutableMap;
import dev.louis.nebula.spell.Spell;
import dev.louis.nebula.spell.SpellType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import java.util.Map;

public class SpellRenderDispatcher implements SynchronousResourceReloader {
    private Map<SpellType<?>, SpellRenderer<?>> renderers = ImmutableMap.of();
    private World world;
    public Camera camera;
    private Quaternionf rotation;
    public Entity targetedEntity;
    public final TextureManager textureManager;
    private final ItemRenderer itemRenderer;
    private final BlockRenderManager blockRenderManager;
    private final TextRenderer textRenderer;
    public final GameOptions gameOptions;

    public SpellRenderDispatcher(
            MinecraftClient client,
            TextureManager textureManager,
            ItemRenderer itemRenderer,
            BlockRenderManager blockRenderManager,
            TextRenderer textRenderer,
            GameOptions gameOptions
    ) {
        this.textureManager = textureManager;
        this.itemRenderer = itemRenderer;
        this.blockRenderManager = blockRenderManager;
        this.textRenderer = textRenderer;
        this.gameOptions = gameOptions;
    }

    public double getSquaredDistanceToCamera(Spell spell) {
        return this.camera.getPos().squaredDistanceTo(spell.getPos());
    }

    public double getSquaredDistanceToCamera(double x, double y, double z) {
        return this.camera.getPos().squaredDistanceTo(x, y, z);
    }

    public void configure(World world, Camera camera, Entity target) {
        this.world = world;
        this.camera = camera;
        this.rotation = camera.getRotation();
        this.targetedEntity = target;
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    @Override
    public void reload(ResourceManager manager) {

    }
}
