package net.caffeinemc.mods.sodium.client.render.chunk;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public record ChunkRenderMatrices(Matrix4fc projection, Matrix4fc modelView) { }