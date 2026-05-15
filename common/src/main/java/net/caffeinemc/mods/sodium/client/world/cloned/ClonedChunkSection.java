package net.caffeinemc.mods.sodium.client.world.cloned;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMaps;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.caffeinemc.mods.sodium.client.services.*;
import net.caffeinemc.mods.sodium.client.util.iterator.WrappedIterator;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.PalettedContainerROExtension;
import net.caffeinemc.mods.sodium.client.world.SodiumAuxiliaryLightManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.util.math.BlockBox;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ClonedChunkSection {
    private static final ChunkNibbleArray DEFAULT_SKY_LIGHT_ARRAY = new ChunkNibbleArray(15);
    private static final ChunkNibbleArray DEFAULT_BLOCK_LIGHT_ARRAY = new ChunkNibbleArray(0);
    private static final PalettedContainer<BlockState> DEFAULT_STATE_CONTAINER = new PalettedContainer<>(Blocks.AIR.defaultBlockState(), Strategy.createForBlockStates(Block.BLOCK_STATE_REGISTRY));

    private final ChunkSectionPos pos;

    private final @Nullable Int2ReferenceMap<BlockEntity> blockEntityMap;
    private final @Nullable Int2ReferenceMap<Object> blockEntityRenderDataMap;

    private final @Nullable ChunkNibbleArray[] lightDataArrays;
    private final @Nullable SodiumAuxiliaryLightManager auxLightManager;

    private final @Nullable ReadableContainer<BlockState> blockData;

    private final @Nullable ReadableContainer<RegistryEntry<Biome>> biomeData;
    private final SodiumModelDataContainer modelMap;

    private long lastUsedTimestamp = Long.MAX_VALUE;

    public ClonedChunkSection(World level, WorldChunk chunk, @Nullable ChunkSection section, ChunkSectionPos pos) {
        this.pos = pos;

        ReadableContainer<BlockState> blockData = null;
        ReadableContainer<RegistryEntry<Biome>> biomeData = null;

        Int2ReferenceMap<BlockEntity> blockEntityMap = null;
        Int2ReferenceMap<Object> blockEntityRenderDataMap = null;
        SodiumModelDataContainer modelMap = PlatformModelAccess.getInstance().getModelDataContainer(level, pos);
        auxLightManager = PlatformLevelAccess.INSTANCE.getLightManager(chunk, pos);

        if (section != null) {
            if (!section.hasOnlyAir()) {
                if (!level.isDebug()) {
                    blockData = PalettedContainerROExtension.clone(section.getStates());
                } else {
                    blockData = constructDebugWorldContainer(pos);
                }
                blockEntityMap = tryCopyBlockEntities(chunk, pos);
                if (blockEntityMap != null && PlatformBlockAccess.getInstance().platformHasBlockData()) {
                    blockEntityRenderDataMap = copyBlockEntityRenderData(level, blockEntityMap);
                }
            }

            biomeData = PalettedContainerROExtension.clone(section.getBiomes());
        }

        this.blockData = blockData;
        this.biomeData = biomeData;
        this.modelMap = modelMap;

        this.blockEntityMap = blockEntityMap;
        this.blockEntityRenderDataMap = blockEntityRenderDataMap;

        this.lightDataArrays = copyLightData(level, pos);
    }

    /**
     * Construct a fake PalettedContainer whose contents match those of the debug world. This is needed to
     * match vanilla's odd approach of short-circuiting getBlockState calls inside its render region class.
     */
    @NonNull
    private static PalettedContainer<BlockState> constructDebugWorldContainer(ChunkSectionPos pos) {
        // Fast path for sections which are guaranteed to be empty
        if (pos.getY() != 3 && pos.getY() != 4)
            return DEFAULT_STATE_CONTAINER;

        // We use swapUnsafe in the loops to avoid acquiring/releasing the lock on each iteration
        var container = new PalettedContainer<>(Blocks.AIR.defaultBlockState(), Strategy.createForBlockStates(Block.BLOCK_STATE_REGISTRY));
        if (pos.getY() == 3) {
            // Set the blocks at relative Y 12 (world Y 60) to barriers
            BlockState barrier = Blocks.BARRIER.defaultBlockState();
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    container.getAndSetUnchecked(x, 12, z, barrier);
                }
            }
        } else if (pos.getY() == 4) {
            // Set the blocks at relative Y 6 (world Y 70) to the appropriate state from the generator
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    container.getAndSetUnchecked(x, 6, z, DebugChunkGenerator.getBlockStateFor(ChunkSectionPos.sectionToBlockCoord(pos.getX(), x), ChunkSectionPos.sectionToBlockCoord(pos.getZ(), z)));
                }
            }
        }
        return container;
    }

    @NonNull
    private static ChunkNibbleArray[] copyLightData(World level, ChunkSectionPos pos) {
        var arrays = new ChunkNibbleArray[2];
        arrays[LightType.BLOCK.ordinal()] = copyLightArray(level, LightType.BLOCK, pos);

        // Dimensions without sky-light should not have a default-initialized array
        if (level.dimensionType().hasSkyLight()) {
            arrays[LightType.SKY.ordinal()] = copyLightArray(level, LightType.SKY, pos);
        }

        return arrays;
    }

    /**
     * Copies the light data array for the given light type for this chunk, or returns a default-initialized value if
     * the light array is not loaded.
     */
    @NonNull
    private static ChunkNibbleArray copyLightArray(World level, LightType type, ChunkSectionPos pos) {
        var array = level.getLightEngine()
                .getLayerListener(type)
                .getDataLayerData(pos);

        if (array == null) {
            array = switch (type) {
                case SKY -> DEFAULT_SKY_LIGHT_ARRAY;
                case BLOCK -> DEFAULT_BLOCK_LIGHT_ARRAY;
            };
        }

        return array;
    }

    @Nullable
    private static Int2ReferenceMap<BlockEntity> tryCopyBlockEntities(WorldChunk chunk, ChunkSectionPos chunkCoord) {
        try {
            // Some mods are violating memory safety, and the block entity iterator occasionally returns garbage results
            // or otherwise throws exceptions because of this. To better diagnose these crashes, wrap the iterator
            // so that we can handle any uncaught exceptions with the following special case.
            return copyBlockEntities(chunk, chunkCoord);
        } catch (WrappedIterator.Exception t) {
            // Very infrequent check, only going to be called on game crash. Don't bother caching this.
            if (PlatformRuntimeInformation.getInstance().isModInLoadingList("entityculling")) {
                // The Entity Culling mod is known to mangle the block entity set, so try to attribute it directly
                // if we know it's installed. Yes, this is accusatory, but we are tired of these cryptic crashes,
                // and users need more information about how to resolve the problem themselves. This was the
                // second-best option to outright preventing the launch of Sodium when Entity Culling is installed.
                throw new RuntimeException("Failed to iterate block entities! This is *very likely* the fault of the " +
                                           "Entity Culling mod, and cannot be fixed by Sodium. See here for more details: " +
                                           "https://link.caffeinemc.net/help/sodium/mod-issue/entity-culling/gh-2985", t);
            } else {
                throw new RuntimeException("Failed to iterate block entities! This is *very likely* the fault of " +
                                           "another misbehaving mod, not Sodium. Please check your mods list.", t);
            }
        }
    }

    @Nullable
    private static Int2ReferenceMap<BlockEntity> copyBlockEntities(WorldChunk chunk, ChunkSectionPos chunkCoord) {
        BlockBox box = new BlockBox(chunkCoord.minBlockX(), chunkCoord.minBlockY(), chunkCoord.minBlockZ(),
                chunkCoord.maxBlockX(), chunkCoord.maxBlockY(), chunkCoord.maxBlockZ());

        Int2ReferenceOpenHashMap<BlockEntity> blockEntities = null;

        // Catch exceptions thrown by the iterator and handle them specially via the wrapped exception type
        var it = WrappedIterator.create(chunk.getBlockEntities().entrySet());

        // Copy the block entities from the chunk into our cloned section
        while (it.hasNext()) {
            var entry = it.next();
            var pos = entry.getKey();
            var entity = entry.getValue();

            if (box.isInside(pos)) {
                if (blockEntities == null) {
                    blockEntities = new Int2ReferenceOpenHashMap<>();
                }

                blockEntities.put(LevelSlice.getLocalBlockIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15), entity);
            }
        }

        if (blockEntities != null) {
            blockEntities.trim();
        }

        return blockEntities;
    }

    @Nullable
    private static Int2ReferenceMap<Object> copyBlockEntityRenderData(World level, Int2ReferenceMap<BlockEntity> blockEntities) {
        Int2ReferenceOpenHashMap<Object> blockEntityRenderDataMap = null;

        // Retrieve any render data after we have copied all block entities, as this will call into the code of
        // other mods. This could potentially result in the chunk being modified, which would cause problems if we
        // were iterating over any data in that chunk.
        // See https://github.com/CaffeineMC/sodium/issues/942 for more info.
        for (var entry : Int2ReferenceMaps.fastIterable(blockEntities)) {
            Object data = PlatformLevelAccess.getInstance().getBlockEntityData(entry.getValue());

            if (data != null) {
                if (blockEntityRenderDataMap == null) {
                    blockEntityRenderDataMap = new Int2ReferenceOpenHashMap<>();
                }

                blockEntityRenderDataMap.put(entry.getIntKey(), data);
            }
        }

        if (blockEntityRenderDataMap != null) {
            blockEntityRenderDataMap.trim();
        }

        return blockEntityRenderDataMap;
    }

    public ChunkSectionPos getPosition() {
        return this.pos;
    }

    public @Nullable ReadableContainer<BlockState> getBlockData() {
        return this.blockData;
    }

    public @Nullable ReadableContainer<RegistryEntry<Biome>> getBiomeData() {
        return this.biomeData;
    }

    public @Nullable Int2ReferenceMap<BlockEntity> getBlockEntityMap() {
        return this.blockEntityMap;
    }

    public @Nullable Int2ReferenceMap<Object> getBlockEntityRenderDataMap() {
        return this.blockEntityRenderDataMap;
    }

    public SodiumModelDataContainer getModelMap() {
        return modelMap;
    }

    public @Nullable ChunkNibbleArray getLightArray(LightType lightType) {
        return this.lightDataArrays[lightType.ordinal()];
    }

    public long getLastUsedTimestamp() {
        return this.lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long timestamp) {
        this.lastUsedTimestamp = timestamp;
    }

    public SodiumAuxiliaryLightManager getAuxLightManager() {
        return auxLightManager;
    }
}
