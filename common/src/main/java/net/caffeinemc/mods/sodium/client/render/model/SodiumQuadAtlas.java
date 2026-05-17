package net.caffeinemc.mods.sodium.client.render.model;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public enum SodiumQuadAtlas {
    BLOCK,
    ITEM;

    public static SodiumQuadAtlas of(ResourceLocation atlasTextureId) {
        if (atlasTextureId.equals(TextureAtlas.LOCATION_BLOCKS)) {
            return BLOCK;
        } else if (atlasTextureId.equals(TextureAtlas.LOCATION_ITEMS)) {
            return ITEM;
        } else {
            return null;
        }
    }
}
