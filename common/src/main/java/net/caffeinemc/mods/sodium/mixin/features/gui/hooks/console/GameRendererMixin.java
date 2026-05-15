package net.caffeinemc.mods.sodium.mixin.features.gui.hooks.console;


import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.gui.console.ConsoleHooks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.util.profiling.Profilers;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    MinecraftClient minecraft;

    @Shadow
    @Final
    private BufferBuilderStorage renderBuffers;

    @Shadow
    @Final
    private GuiRenderState guiRenderState;
    @Unique
    private static boolean HAS_RENDERED_OVERLAY_ONCE = false;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
    private void onRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        // Do not start updating the console overlay until the font renderer is ready
        // This prevents the console from using tofu boxes for everything during early startup
        if (MinecraftClient.getInstance().getOverlay() != null) {
            if (!HAS_RENDERED_OVERLAY_ONCE) {
                return;
            }
        }

        Profilers.get().push("sodium_console_overlay");
        int mouseX = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
        int mouseY = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
        DrawContext drawContext = new DrawContext(this.minecraft, this.guiRenderState, mouseX, mouseY);

        ConsoleHooks.render(drawContext, GLFW.glfwGetTime());

        Profilers.get().pop();

        HAS_RENDERED_OVERLAY_ONCE = true;
    }
}
