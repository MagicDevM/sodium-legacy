package net.caffeinemc.mods.sodium.mixin.features.render.model.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.client.model.color.interop.ItemColorsExtended;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.BakedQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import net.caffeinemc.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.client.util.DirectionUtil;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static final ThreadLocal<RandomSource> random = ThreadLocal.withInitial(() -> new SingleThreadedRandomSource(42L));

    @Shadow
    private static int getLayerColorSafe(int[] is, int i) {
        throw new AssertionError("Not shadowed");
    }

    @Shadow
    @Final
    private ItemColors itemColors;

    /**
     * @reason Avoid Allocations
     * @return JellySquid
     */
    @WrapOperation(method = "renderModelList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;[III)V"))
    private void renderModelFast(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> quads, ItemStack itemStack, int light, int overlay, Operation<Void> original) {
        var writer = VertexConsumerUtils.convertOrLog(vertexConsumer);

        if (writer == null) {
            original.call(poseStack, vertexConsumer, quads, itemStack, light, overlay);
            return;
        }

        ItemColor colorProvider = null;

        // TODO/NOTE: Should .last be a LocalRef?
        if (!quads.isEmpty()) {
            colorProvider = ((ItemColorsExtended) this.itemColors).sodium$getColorProvider(itemStack);
            
            renderBakedItemQuads(poseStack.last(), writer, quads, itemStack, colorProvider, light, overlay);
        }
    }

    @Unique
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void renderBakedItemQuads(PoseStack.Pose matrices, VertexBufferWriter writer, List<BakedQuad> quads, ItemStack itemStack, ItemColor colorProvider, int light, int overlay) {
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad bakedQuad = quads.get(i);

            BakedQuadView quad = (BakedQuadView) (Object) bakedQuad;

            int color = 0xFFFFFFFF;

            if (bakedQuad.isTinted()) {
                color = ColorARGB.toABGR((colorProvider.getColor(itemStack, quad.getTintIndex())), 255);
            }

            BakedModelEncoder.writeQuadVertices(writer, matrices, quad, color, light, overlay, BakedModelEncoder.shouldMultiplyAlpha());

            if (quad.getSprite() != null) {
                SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
            }
        }
    }
}
