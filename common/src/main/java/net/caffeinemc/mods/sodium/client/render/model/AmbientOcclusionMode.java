package net.caffeinemc.mods.sodium.client.render.model;

public enum AmbientOcclusionMode {
    ENABLED,
    DEFAULT,
    DISABLED;

    public enum TriState {
        TRUE,
        FALSE,
        DEFAULT
    };
    
    private static final TriState[] TRISTATES = new TriState[] {
        TriState.TRUE,    // ENABLED
        TriState.DEFAULT, // DEFAULT
        TriState.FALSE    // DISABLED
    };

    public TriState toTriState() {
        return TRISTATES[this.ordinal()];
    }
}
