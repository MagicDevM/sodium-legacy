package net.caffeinemc.mods.sodium.client.world.biome;

import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;

public class BiomeColorMaps {
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;

    private static final int INVALID_INDEX = -1;

    public static int getGrassColor(int index) {
        if (index == INVALID_INDEX || index >= GrassColors.pixels.length) {
            return GrassColors.getDefaultColor();
        }

        return GrassColors.pixels[index];
    }

    public static int getFoliageColor(int index) {
        if (index == INVALID_INDEX || index >= FoliageColors.pixels.length) {
            return FoliageColors.FOLIAGE_DEFAULT;
        }

        return FoliageColors.pixels[index];
    }

    public static int getIndex(double temperature, double humidity) {
        humidity *= temperature;

        int x = (int) ((1.0D - temperature) * 255.0D);
        int y = (int) ((1.0D - humidity) * 255.0D);

        if (x < 0 || x >= WIDTH) {
            return INVALID_INDEX;
        }

        if (y < 0 || y >= HEIGHT) {
            return INVALID_INDEX;
        }

        return (y << 8) | x;
    }
}
