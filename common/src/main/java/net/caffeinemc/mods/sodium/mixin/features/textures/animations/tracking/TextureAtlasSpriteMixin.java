package net.caffeinemc.mods.sodium.mixin.features.textures.animations.tracking;

import net.minecraft.client.render.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sprite.class)
public abstract class TextureAtlasSpriteMixin {
    @Inject(method = "wrap", at = @At("HEAD"))
    private void markSpriteAsActive(CallbackInfoReturnable<VertexConsumer> cir) {
        SpriteUtil.INSTANCE.markSpriteActive((Sprite) (Object) this);
    }
}
