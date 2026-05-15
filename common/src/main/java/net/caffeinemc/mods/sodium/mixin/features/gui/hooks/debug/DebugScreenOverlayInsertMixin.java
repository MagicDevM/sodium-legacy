package net.caffeinemc.mods.sodium.mixin.features.gui.hooks.debug;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.util.FrameTimeStatistics;
import net.minecraft.util.Formatting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugScreenOverlayInsertMixin {
    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;renderLines(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V", ordinal = 0)
    )
    private void sodium$insertFpsPercentiles(DrawContext guiGraphics, CallbackInfo ci, @Local(ordinal = 0) List<String> leftLines) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (!minecraft.debugEntries.isCurrentlyEnabled(SodiumClientMod.SODIUM_FPS_PERCENTILES)) {
            return;
        }
        var results = FrameTimeStatistics.INSTANCE.get();
        if (results == null || results.isEmpty()) {
            return;
        }

        // splice the percentile fps display into the debug lines to make sure it's right under the fps string.
        // without this, it may be put somewhere else on the screen.
        int insertAt = 0;
        for (int i = 0; i < leftLines.size(); i++) {
            String line = leftLines.get(i);
            if (line != null && line.contains(" fps T:")) {
                insertAt = i + 1;
                break;
            }
        }

        var sb = new StringBuilder();
        for (var entry : results.reference2LongEntrySet()) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            long ns = entry.getLongValue();
            sb.append(Formatting.GRAY)
                    .append(entry.getKey().name()).append('=')
                    .append(Formatting.RESET)
                    .append(sodium$nanosToFps(ns));
        }

        sb.append(Formatting.GRAY).append(" fps");

        leftLines.add(insertAt, sb.toString());
    }

    @Unique
    private static long sodium$nanosToFps(long ns) {
        return ns > 0L ? Math.round(1.0e9 / ns) : 0L;
    }
}
