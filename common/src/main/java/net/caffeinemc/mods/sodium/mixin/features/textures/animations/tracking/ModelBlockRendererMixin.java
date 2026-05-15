package net.caffeinemc.mods.sodium.mixin.features.textures.animations.tracking;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.render.block.ModelBlockRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    /**
     * @reason Ensure sprites rendered through renderSmooth/renderFlat in immediate-mode are marked as active.
     * This doesn't affect vanilla to my knowledge, but mods can trigger it.
     * @author embeddedt
     */
    @Inject(method = "putQuadData", at = @At("HEAD"))
    private void preRenderQuad(BlockRenderView blockAndTintGetter, BlockState blockState, BlockPos blockPos, VertexConsumer vertexConsumer, MatrixStack.Pose pose, BakedQuad quad, @Coerce Object commonRenderStorage, int i, CallbackInfo ci) {
        if (quad.sprite() != null) {
            SpriteUtil.INSTANCE.markSpriteActive(quad.sprite());
        }
    }
}
