package net.caffeinemc.mods.sodium.mixin.features.textures.animations.tracking;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "blit(IIIIILcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", at = @At("HEAD"))
    private void preDrawSprite(int x, int y, int z, int width, int height, TextureAtlasSprite sprite, CallbackInfo ci) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }

    @Inject(method = "blitSprite(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;FFFF)V", at = @At("HEAD"))
    private void preDrawSprite(int x, int y, int z, int width, int height, TextureAtlasSprite sprite, float red, float green, float blue, float alpha, CallbackInfo ci) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }
}
