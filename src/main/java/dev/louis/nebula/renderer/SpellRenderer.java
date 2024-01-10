package dev.louis.nebula.renderer;

import dev.louis.nebula.spell.Spell;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.joml.Matrix4f;

public class SpellRenderer<T extends Spell> {
    protected final SpellRenderDispatcher dispatcher;
    protected final TextRenderer textRenderer;

    protected SpellRenderer(SpellRendererFactory.Context ctx) {
        this.dispatcher = ctx.getRenderDispatcher();
        this.textRenderer = ctx.getTextRenderer();
    }

    public boolean shouldRender(T spell, Frustum frustum, double x, double y, double z) {
        return true;
        /*if (!entity.shouldRender(x, y, z)) {
            return false;
        } else if (entity.ignoreCameraFrustum) {
            return true;
        } else {
            Box box = entity.getVisibilityBoundingBox().expand(0.5);
            if (box.isNaN() || box.getAverageSideLength() == 0.0) {
                box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
            }

            return frustum.isVisible(box);
        }*/
    }

    public void render(T spell, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (this.renderDebug()) {
            this.renderDebugLabel(spell, Text.of(spell.getID()), matrices, vertexConsumers, light);
        }
    }

    protected void renderDebugLabel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            float f = 2.5f;
            int i = 0;
            matrices.push();
            matrices.translate(0.0F, f, 0.0F);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = this.getTextRenderer();
            float h = (float)(-textRenderer.getWidth(text) / 2);
            textRenderer.draw(
                    text, h, (float)i, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, j, light
            );
            textRenderer.draw(text, h, (float) i, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

            matrices.pop();
        }
    }

    private boolean renderDebug() {
        return true;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }
}
