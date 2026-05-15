package net.caffeinemc.mods.sodium.mixin.features.textures.animations.tracking;

import com.mojang.blaze3d.systems.RenderCallStorage;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(DrawContext.class)
public class GuiGraphicsMixin {

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/systems/RenderCallStorage;Lnet/minecraft/client/texture/Sprite;IIIII)V", at = @At("HEAD"))
    private void preDrawSprite(RenderCallStorage renderPipeline, TextureAtlasSprite sprite, int x, int y, int width, int height, int blitOffset, CallbackInfo ci) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/systems/RenderCallStorage;Lnet/minecraft/client/texture/Sprite;IIIIIIIII)V", at = @At("HEAD"))
    private void preDrawSprite(RenderCallStorage renderPipeline, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight, int blitOffset, CallbackInfo ci) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }
}
