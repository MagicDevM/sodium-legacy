package net.caffeinemc.mods.sodium.mixin.features.render.model.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.BakedQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import net.caffeinemc.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.client.util.DirectionUtil;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.LocalRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique
    private static final ThreadLocal<Random> random = ThreadLocal.withInitial(() -> new LocalRandom(42L));

    @Shadow
    private static int getLayerColorSafe(int[] is, int i) {
        throw new AssertionError("Not shadowed");
    }

    /**
     * @reason Avoid Allocations
     * @return JellySquid
     */
    @WrapOperation(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Ljava/util/List;[III)V"))
    private static void renderModelFast(MatrixStack MatrixStack, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] colors, int light, int overlay, Operation<Void> original) {
        var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        if (writer == null) {
            original.call(MatrixStack, vertexConsumer, quads, colors, light, overlay);
            return;
        }

        // TODO/NOTE: Should .last be a LocalRef?
        if (!quads.isEmpty()) {
            renderBakedItemQuads(MatrixStack.last(), writer, quads, colors, light, overlay);
        }
    }

    @Unique
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private static void renderBakedItemQuads(MatrixStack.Entry matrices, VertexBufferWriter writer, List<BakedQuad> quads, int[] colors, int light, int overlay) {
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad bakedQuad = quads.get(i);

            BakedQuadView quad = (BakedQuadView) (Object) bakedQuad;

            int color = 0xFFFFFFFF;

            if (bakedQuad.isTinted()) {
                color = ColorARGB.toABGR(getLayerColorSafe(colors, bakedQuad.tintIndex()));
            }

            BakedModelEncoder.writeQuadVertices(writer, matrices, quad, color, light, overlay, BakedModelEncoder.shouldMultiplyAlpha());

            if (quad.getSprite() != null) {
                SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
            }
        }
    }
}
