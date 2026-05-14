package net.caffeinemc.mods.sodium.api.blockentity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("0.6.0")
@FunctionalInterface
public interface BlockEntityRenderPredicate<T extends BlockEntity> {
    boolean shouldRender(BlockView blockGetter, BlockPos blockPos, T entity);
}
