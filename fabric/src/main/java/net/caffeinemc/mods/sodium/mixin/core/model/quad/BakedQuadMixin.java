package net.caffeinemc.mods.sodium.mixin.core.model.quad;

import net.caffeinemc.mods.sodium.client.model.quad.BakedQuadView;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFlags;
import net.caffeinemc.mods.sodium.client.util.ModelQuadUtil;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BakedQuad.class)
public abstract class BakedQuadMixin implements BakedQuadView {
    @Shadow
    @Final
    private boolean shade;

    @Shadow
    public abstract int lightEmission();

    @Shadow
    public abstract int[] getVertices();

    @Shadow
    @Final
    private TextureAtlasSprite sprite;

    @Shadow
    @Final
    private int tintIndex;
    @Shadow
    @Final
    private Direction direction;
    @Unique
    private int flags;

    @Unique
    private int normal;

    @Unique
    private int[] vertices;

    @Unique
    private ModelQuadFacing normalFace = null;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, long l, long m, long n, long o, int i, Direction direction, TextureAtlasSprite textureAtlasSprite, boolean bl, int j, CallbackInfo ci) {
        this.normal = this.calculateNormal();
        this.normalFace = ModelQuadFacing.fromPackedNormal(this.normal);

        this.flags = ModelQuadFlags.getQuadFlags(this, direction);
        this.vertices = this.getVertices();
    }

    @Override
    public float getX(int idx) {
        return Float.intBitsToFloat(vertices[idx * 8]);
    }

    @Override
    public float getY(int idx) {
        return Float.intBitsToFloat(vertices[idx * 8 + 1]);
    }

    @Override
    public float getZ(int idx) {
        return Float.intBitsToFloat(vertices[idx * 8 + 2]);
    }

    @Override
    public int getColor(int idx) {
        return this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.COLOR_INDEX];
    }

    @Override
    public int getVertexNormal(int idx) {
        return this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.NORMAL_INDEX];
    }

    @Override
    public int getLight(int idx) {
        return this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.LIGHT_INDEX];
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return this.sprite;
    }

    @Override
    public float getTexU(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.TEXTURE_INDEX]);
    }

    @Override
    public float getTexV(int idx) {
        return Float.intBitsToFloat(this.vertices[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.TEXTURE_INDEX + 1]);
    }

    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public int getTintIndex() {
        return this.tintIndex;
    }

    @Override
    public ModelQuadFacing getNormalFace() {
        return this.normalFace;
    }

    @Override
    public int getFaceNormal() {
        return this.normal;
    }

    @Override
    public Direction getLightFace() {
        return this.direction;
    }

    @Override
    public int getMaxLightQuad(int idx) {
        return this.getLight(idx);
    }

    @Override
    @Unique(silent = true) // The target class has a function with the same name in a remapped environment
    public boolean hasShade() {
        return this.shade;
    }

    @Override
    public boolean hasAO() {
        return true;
    }
}
