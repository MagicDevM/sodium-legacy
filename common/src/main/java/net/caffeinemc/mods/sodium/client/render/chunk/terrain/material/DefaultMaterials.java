package net.caffeinemc.mods.sodium.client.render.chunk.terrain.material;

import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class DefaultMaterials {
    public static final Material SOLID = new Material(DefaultTerrainRenderPasses.SOLID, AlphaCutoffParameter.ZERO, true);
    public static final Material CUTOUT_MIPPED = new Material(DefaultTerrainRenderPasses.CUTOUT, AlphaCutoffParameter.HALF, true);
    public static final Material TRANSLUCENT = new Material(DefaultTerrainRenderPasses.TRANSLUCENT, AlphaCutoffParameter.TINY, true);
    public static final Material TRIPWIRE = new Material(DefaultTerrainRenderPasses.TRANSLUCENT, AlphaCutoffParameter.TINY, true);

    public static Material forBlockState(BlockState state) {
        return forChunkLayer(ItemBlockRenderTypes.getChunkRenderType(state));
    }

    public static Material forFluidState(FluidState state) {
        return forChunkLayer(ItemBlockRenderTypes.getRenderLayer(state));
    }

    public static Material forChunkLayer(RenderType layer) {
        if (layer == RenderType.solid()) {
            return SOLID;
        } else if (layer == RenderType.cutoutMipped()) {
            return CUTOUT_MIPPED;
        } else if (layer == RenderType.translucent()) {
            return TRANSLUCENT;
        } else if (layer == RenderType.tripwire()) {
            return TRIPWIRE;
        }

        throw new IllegalArgumentException("No material mapping exists for " + layer);
    }
}
