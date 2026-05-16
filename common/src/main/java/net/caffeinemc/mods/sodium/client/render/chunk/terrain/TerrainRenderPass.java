package net.caffeinemc.mods.sodium.client.render.chunk.terrain;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class TerrainRenderPass {
    @Deprecated(forRemoval = true)
    private final RenderType renderType;

    private final boolean isTranslucent;
    private final boolean fragmentDiscard;

    public TerrainRenderPass(RenderType renderType, boolean isTranslucent, boolean allowFragmentDiscard) {
        this.renderType = renderType;

        this.isTranslucent = isTranslucent;
        this.fragmentDiscard = allowFragmentDiscard;
    }

    public boolean isTranslucent() {
        return this.isTranslucent;
    }

    public boolean supportsFragmentDiscard() {
        return this.fragmentDiscard;
    }

    public RenderPipeline getPipeline() {
        return renderType.pipeline();
    }

    public RenderTarget getTarget() {
        return (isTranslucent && Minecraft.useShaderTransparency()) ? Minecraft.getInstance().levelRenderer.getTranslucentTarget() : Minecraft.getInstance().getMainRenderTarget();
    }

    public AbstractTexture getAtlas() {
        return Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).getTextureView();
    }
}
