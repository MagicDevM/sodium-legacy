package net.caffeinemc.mods.sodium.mixin.frapi;

import com.mojang.blaze3d.vertex.MatrixStack;
import net.caffeinemc.mods.sodium.client.render.frapi.render.ItemRenderContext;
import net.caffeinemc.mods.sodium.client.render.frapi.render.MeshItemCommand;
import net.caffeinemc.mods.sodium.client.render.frapi.render.SubmitNodeCollectionExtension;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFeatureRenderer.class)
public class ItemFeatureRendererMixin {
    @Shadow
    @Final
    private MatrixStack MatrixStack;

    @Unique
    private final ItemRenderContext itemRenderContext = new ItemRenderContext();

    @Inject(method = "render", at = @At("RETURN"))
    private void onReturnRender(SubmitNodeCollection submitNodeCollection, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, CallbackInfo ci) {
        for (MeshItemCommand itemCommand : ((SubmitNodeCollectionExtension) submitNodeCollection).sodium_getMeshItemCommands()) {
            MatrixStack.pushPose();
            MatrixStack.last().set(itemCommand.positionMatrix());

            itemRenderContext.renderItem(itemCommand.displayContext(), MatrixStack, bufferSource, itemCommand.lightCoords(), itemCommand.overlayCoords(), itemCommand.tintLayers(), itemCommand.quads(), itemCommand.mesh(), itemCommand.renderType(), itemCommand.glintType(), itemCommand.renderTypeGetter(), false);

            if (itemCommand.outlineColor() != 0) {
                outlineBufferSource.setColor(itemCommand.outlineColor());
                itemRenderContext.renderItem(itemCommand.displayContext(), MatrixStack, outlineBufferSource, itemCommand.lightCoords(), itemCommand.overlayCoords(), itemCommand.tintLayers(), itemCommand.quads(), itemCommand.mesh(), itemCommand.renderType(), ItemStackRenderState.FoilType.NONE, itemCommand.renderTypeGetter(), true);
            }

            MatrixStack.popPose();
        }
    }
}
