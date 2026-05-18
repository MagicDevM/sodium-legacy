package net.caffeinemc.mods.sodium.client.render.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;

/**
 * Caches {@link SpriteFinder}s for maximum efficiency. They must be refreshed after each resource reload.
 *
 * <p><b>This class should not be used during a resource reload</b>, as returned SpriteFinders may be null or outdated.
 */
public class SpriteFinderCache {
    private static SodiumSpriteFinder blockAtlasSpriteFinder;
    private static SodiumSpriteFinder itemAtlasSpriteFinder;

    public static SodiumSpriteFinder forBlockAtlas() {
        if (blockAtlasSpriteFinder == null) {
            blockAtlasSpriteFinder = ((ExtendedTextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).sodium$getSpriteFinder();
        }

        return blockAtlasSpriteFinder;
    }

    public static SodiumSpriteFinder forItemAtlas() {
        if (itemAtlasSpriteFinder == null) {
            itemAtlasSpriteFinder = ((ExtendedTextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).sodium$getSpriteFinder();
        }

        return itemAtlasSpriteFinder;
    }

    public static void resetSpriteFinder() {
        blockAtlasSpriteFinder = null;
    }

    public static void resetItemSpriteFinder() {
        itemAtlasSpriteFinder = null;
    }
}
