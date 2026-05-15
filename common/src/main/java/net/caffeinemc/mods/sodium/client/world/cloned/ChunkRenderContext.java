package net.caffeinemc.mods.sodium.client.world.cloned;

import net.caffeinemc.mods.sodium.client.services.SodiumModelDataContainer;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.BlockBox;

import java.util.List;

public class ChunkRenderContext {
    private final ChunkSectionPos origin;
    private final ClonedChunkSection[] sections;
    private final ChunkSectionPos volume;
    private final List<?> renderers;

    public ChunkRenderContext(ChunkSectionPos origin, ClonedChunkSection[] sections, ChunkSectionPos volume, List<?> renderers) {
        this.origin = origin;
        this.sections = sections;
        this.volume = volume;
        this.renderers = renderers;
    }

    public ClonedChunkSection[] getSections() {
        return this.sections;
    }

    public BlockBox getOrigin() {
        return this.origin;
    }

    public BlockBox getVolume() {
        return this.volume;
    }

    public List<?> getRenderers() {
        return renderers;
    }
}
