package net.caffeinemc.mods.sodium.mixin.features.textures.animations.tracking;

import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.font.AtlasGlyphProvider$Instance")
public class AtlasGlyphProviderMixin {

    @Shadow
    @Final
    private Sprite sprite;

    @Inject(method = "renderSprite", at = @At("HEAD"))
    private void preRenderSprite(CallbackInfo ci) {
        SpriteUtil.INSTANCE.markSpriteActive(this.sprite);
    }
}
