package net.caffeinemc.mods.sodium.client.render.chunk.terrain;

import net.minecraft.client.renderer.RenderType;

public class DefaultTerrainRenderPasses {
    public static final TerrainRenderPass SOLID = new TerrainRenderPass(RenderType.SOLID, false, false);
    public static final TerrainRenderPass CUTOUT = new TerrainRenderPass(RenderType.CUTOUT, false, true);
    public static final TerrainRenderPass TRANSLUCENT = new TerrainRenderPass(RenderType.TRANSLUCENT, true, true);


    public static final TerrainRenderPass[] ALL = new TerrainRenderPass[] { SOLID, CUTOUT, TRANSLUCENT };
}
