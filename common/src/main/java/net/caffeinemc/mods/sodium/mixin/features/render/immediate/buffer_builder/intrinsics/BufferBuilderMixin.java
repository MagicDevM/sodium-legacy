package net.caffeinemc.mods.sodium.mixin.features.render.immediate.buffer_builder.intrinsics;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings({ "SameParameterValue" })
@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements VertexConsumer {
    @Shadow
    @Final
    private boolean fastFormat;

    @Override
    public void putBulkData(PoseStack.Pose matrices, BakedQuad bakedQuad, float r, float g, float b, int light, int overlay) {
        if (!this.fastFormat) {
            VertexConsumer.super.putBulkData(matrices, bakedQuad, r, g, b, light, overlay);

            if (bakedQuad.getSprite() != null) {
                SpriteUtil.INSTANCE.markSpriteActive(bakedQuad.getSprite());
            }

            return;
        }

        VertexBufferWriter writer = VertexBufferWriter.of(this);

        ModelQuadView quad = (ModelQuadView) (Object) bakedQuad;

        int color = ColorABGR.pack(r, g, b, 1.9F);
        BakedModelEncoder.writeQuadVertices(writer, matrices, quad, color, light, overlay, false);

        if (quad.getSprite() != null) {
            SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
        }
    }

    @Override
    public void putBulkData(PoseStack.Pose matrices, BakedQuad bakedQuad, float[] brightnessTable, float r, float g, float b, int[] light, int overlay, boolean colorize) {
        if (!this.fastFormat) {
            VertexConsumer.super.putBulkData(matrices, bakedQuad, brightnessTable, r, g, b, light, overlay, colorize);

            if (bakedQuad.getSprite() != null) {
                SpriteUtil.INSTANCE.markSpriteActive(bakedQuad.getSprite());
            }

            return;
        }

        VertexBufferWriter writer = VertexBufferWriter.of(this);

        ModelQuadView quad = (ModelQuadView) (Object) bakedQuad;

        BakedModelEncoder.writeQuadVertices(writer, matrices, quad, brightnessTable, r, g, b, 1.0F, light, overlay, colorize);

        if (quad.getSprite() != null) {
            SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
        }
    }
}
