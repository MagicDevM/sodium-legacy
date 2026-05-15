package net.caffeinemc.mods.sodium.mixin.features.render.immediate.buffer_builder.sorting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.system.VertexSorter;
import net.caffeinemc.mods.sodium.client.util.sorting.VertexSorters;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VertexSorter.class)
public interface VertexSortingMixin {
    @SuppressWarnings("DiscouragedShift") // Not currently avoidable.
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/mojang/blaze3d/system/VertexSorter;ORTHOGRAPHIC_Z:Lcom/mojang/blaze3d/system/VertexSorter;",
                    opcode = Opcodes.PUTSTATIC,
                    shift = At.Shift.BEFORE))
    private static VertexSorter modifyVertexSorting(VertexSorter original) {
        return VertexSorters.orthographicZ();
    }

    /**
     * @author JellySquid
     * @reason Optimize vertex sorting
     */
    @Overwrite
    static VertexSorter byDistance(float x, float y, float z) {
        return VertexSorters.distance(x, y, z);
    }

    /**
     * @author JellySquid
     * @reason Optimize vertex sorting
     */
    @Overwrite
    static VertexSorter byDistance(VertexSorter.DistanceFunction function) {
        return VertexSorters.fallback(function);
    }
}