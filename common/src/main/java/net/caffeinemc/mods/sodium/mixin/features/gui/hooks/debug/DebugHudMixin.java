package net.caffeinemc.mods.sodium.mixin.features.gui.hooks.debug;

import com.google.common.collect.Lists;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.util.MathUtil;
import net.caffeinemc.mods.sodium.client.util.NativeBuffer;
import net.caffeinemc.mods.sodium.client.util.FrameTimeStatistics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugHudMixin {
    @Redirect(method = "getSystemInformation", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;", remap = false))
    private ArrayList<String> redirectRightTextEarly(Object[] elements) {
        ArrayList<String> strings = Lists.newArrayList((String[]) elements);
        strings.add("");
        strings.add("%sSodium Renderer (%s)".formatted(getVersionColor(), SodiumClientMod.getVersion()));

        var renderer = SodiumWorldRenderer.instanceNullable();

        if (renderer != null) {
            strings.addAll(renderer.getDebugStrings());
        }

        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);

            if (str.startsWith("Allocated:")) {
                strings.add(i + 1, getNativeMemoryString());

                break;
            }
        }

        return strings;
    }
    
    @Redirect(method = "getGameInformation", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;", remap = false))
    private ArrayList<String> redirectLeftTextEarly(Object[] elements, GuiGraphics guiGraphics, CallbackInfo ci, @Local(ordinal = 0) List<String> leftLines) {
        ArrayList<String> strings = Lists.newArrayList((String[]) elements);
        strings.add("");
        
        Minecraft minecraft = Minecraft.getInstance();
        
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
            sb.append(ChatFormatting.GRAY)
                    .append(entry.getKey().name()).append('=')
                    .append(ChatFormatting.RESET)
                    .append(sodium$nanosToFps(ns));
        }

        sb.append(ChatFormatting.GRAY).append(" fps");

        leftLines.add(insertAt, sb.toString());
        
        return leftLines;
    }
    
    @Unique
    private static ChatFormatting getVersionColor() {
        String version = SodiumClientMod.getVersion();
        ChatFormatting color;

        if (version.contains("-local")) {
            color = ChatFormatting.RED;
        } else if (version.contains("-snapshot")) {
            color = ChatFormatting.LIGHT_PURPLE;
        } else {
            color = ChatFormatting.GREEN;
        }

        return color;
    }

    @Unique
    private static String getNativeMemoryString() {
        return "Off-Heap: +" + MathUtil.toMib(getNativeMemoryUsage()) + "MB";
    }

    @Unique
    private static long getNativeMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() + NativeBuffer.getTotalAllocated();
    }
}