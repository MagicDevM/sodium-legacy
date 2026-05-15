package net.caffeinemc.mods.sodium.mixin.features.textures.animations.tracking;

import net.minecraft.client.render.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.texture.Sprite;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BillboardParticle.class)
public abstract class TextureSheetParticleMixin {
    @Shadow
    protected Sprite sprite;

    @Unique
    private boolean shouldTickSprite;

    @Inject(method = "setSprite(Lnet/minecraft/client/texture/Sprite;)V", at = @At("RETURN"))
    private void afterSetSprite(Sprite sprite, CallbackInfo ci) {
        this.shouldTickSprite = sprite != null && SpriteUtil.INSTANCE.hasAnimation(sprite);
    }

    @Inject(method = "extractRotatedQuad(Lnet/minecraft/client/renderer/state/QuadParticleRenderState;Lorg/joml/Quaternionf;FFFF)V", at = @At("HEAD"))
    private void sodium$tickSprite(QuadParticleRenderState quadParticleRenderState, Quaternionf quaternionf, float f, float g, float h, float i, CallbackInfo ci) {
        if (shouldTickSprite) {
            SpriteUtil.INSTANCE.markSpriteActive(sprite);
        }
    }
}