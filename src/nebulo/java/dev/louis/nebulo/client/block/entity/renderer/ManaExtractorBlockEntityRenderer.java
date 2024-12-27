package dev.louis.nebulo.client.block.entity.renderer;

import dev.louis.nebula.api.mana.helper.ManaHelper;
import dev.louis.nebulo.block.entity.ManaExtractorBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ManaExtractorBlockEntityRenderer implements BlockEntityRenderer<ManaExtractorBlockEntity> {
    private final TextRenderer textRenderer;
    private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, 0.33333334F, 0.046666667F);

    public ManaExtractorBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(ManaExtractorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 4, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getWorld().getTime()));
        renderText(Text.literal(ManaHelper.formatKilomana(entity.manaContainer.getMana())), matrices, vertexConsumers, light);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        renderText(Text.literal(ManaHelper.formatKilomana(entity.manaContainer.getMana())), matrices, vertexConsumers, light);
        matrices.pop();
    }

    private void setTextAngles(MatrixStack matrices, Vec3d translation) {
        float scale = 0.15625F;
        matrices.translate(translation.x, translation.y, translation.z);
        matrices.scale(scale, -scale, scale);
    }


    void renderText(
            Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light
    ) {
        matrices.push();
        this.setTextAngles(matrices, TEXT_OFFSET);
        int color = Colors.LIGHT_RED;



        OrderedText orderedText = text.asOrderedText();
        float x = (float)(-this.textRenderer.getWidth(orderedText) / 2);
        this.textRenderer
                .draw(
                        orderedText,
                        x,
                        (float) (10),
                        color,
                        false,
                        matrices.peek().getPositionMatrix(),
                        vertexConsumers,
                        TextRenderer.TextLayerType.POLYGON_OFFSET,
                        0,
                        light
                );

        matrices.pop();
    }
}
