package net.caffeinemc.mods.sodium.mixin.features.render.entity.cull;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @WrapOperation(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/culling/Frustum;isVisible(Lnet/minecraft/util/math/Box;)Z", ordinal = 0))
    private boolean preShouldRender(Frustum instance, Box aABB, Operation<Boolean> original, T entity) {
        var renderer = SodiumWorldRenderer.instanceNullable();

        if (renderer == null) {
            return original.call(instance, aABB);
        }

        return renderer.isEntityVisible((EntityRenderer<T, S>) (Object) this, entity) && original.call(instance, aABB);
    }
}
