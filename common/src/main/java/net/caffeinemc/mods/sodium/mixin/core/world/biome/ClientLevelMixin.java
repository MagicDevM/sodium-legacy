package net.caffeinemc.mods.sodium.mixin.core.world.biome;

import net.caffeinemc.mods.sodium.client.world.BiomeSeedProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientLevelMixin implements BiomeSeedProvider {
    @Unique
    private long biomeZoomSeed;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void captureSeed(ClientPlayNetworkHandler packetListener,
                             ClientWorld.ClientLevelData levelData,
                             RegistryKey<World> dimension,
                             RegistryEntry<DimensionType> dimensionType,
                             int loadDistance,
                             int simulationDistance,
                             WorldRenderer renderer,
                             boolean isDebug,
                             long biomeZoomSeed, int k,
                             CallbackInfo ci) {
        this.biomeZoomSeed = biomeZoomSeed;
    }

    @Override
    public long sodium$getBiomeZoomSeed() {
        return this.biomeZoomSeed;
    }
}