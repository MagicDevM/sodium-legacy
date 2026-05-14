package net.caffeinemc.mods.sodium.client.config.structure;

import net.minecraft.utils.Identifier;

public record OptionOverride(Identifier target, String source, Option change) {
}
