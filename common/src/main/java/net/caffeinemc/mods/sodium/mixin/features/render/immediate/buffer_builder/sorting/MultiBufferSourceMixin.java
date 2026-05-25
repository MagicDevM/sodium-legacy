package net.caffeinemc.mods.sodium.mixin.features.render.immediate.buffer_builder.sorting;

import java.nio.ByteBuffer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.caffeinemc.mods.sodium.client.util.sorting.VertexSorters;
import net.caffeinemc.mods.sodium.client.util.sorting.VertexSortingExtended;
import net.minecraft.client.renderer.MultiBufferSource;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiBufferSource.BufferSource.class)
public class MultiBufferSourceMixin {
    @Unique
    private static final int VERTICES_PER_QUAD = 6;
    
    @WrapOperation(
        method = "endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;getSortState()Lcom/mojang/blaze3d/vertex/BufferBuilder$SortState;"
        )
    )
    private BufferBuilder.SortState redirectSortQuads(BufferBuilder bufferBuilder, VertexSorting sorting, Operation<BufferBuilder.SortState> original) {
        if (sorting instanceof VertexSortingExtended sortingExtended) {
            // Replace the vertex sorting algorithm when it implements our accelerated sort.
            acceleratedSort(bufferBuilder, sortingExtended);
        } else {
            return original.call(bufferBuilder, sorting);
        }

        // The caller never uses the return value.
        return null;
    }
    
    @Unique
    private static void acceleratedSort(BufferBuilder bufferBuilder, VertexSortingExtended sorting) {
        if (bufferBuilder.mode != VertexFormat.Mode.QUADS) {
            // Only quad lists can be sorted.
            return;
        }

        var sortedPrimitiveIds = VertexSorters.sort(bufferBuilder.buffer, bufferBuilder.vertices, bufferBuilder.format.getVertexSize(), sorting);
        
        reorderQuadVertices(
            bufferBuilder.buffer,
            sortedPrimitiveIds,
            bufferBuilder.format.getVertexSize()
          );
    }
    
    // Reorder Quad vertices to free up memory
    @Unique
    private static void reorderQuadVertices(ByteBuffer buffer, int[] primitiveIds, int vertexSize) {
        int quadSize = vertexSize * 4;
        
        ByteBuffer copy = MemoryUtil.memAlloc(buffer.capacity());
        
        for (int dstQuad = 0; dstQuad < primitiveIds.length; dstQuad++) {
        int srcQuad = primitiveIds[dstQuad];

        int srcOffset = srcQuad * quadSize;
        int dstOffset = dstQuad * quadSize;

        MemoryUtil.memCopy(
            MemoryUtil.memAddress(buffer) + srcOffset,
            MemoryUtil.memAddress(copy) + dstOffset,
            quadSize
          );
        }
    
        MemoryUtil.memCopy(
            MemoryUtil.memAddress(copy),
            MemoryUtil.memAddress(buffer),
            (long) primitiveIds.length * quadSize
        );
    
        MemoryUtil.memFree(copy);
    }
}
