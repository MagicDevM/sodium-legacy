package net.caffeinemc.mods.sodium.client.util.sorting;

import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Vector3f;

public interface VertexSortingExtended extends VertexSorter {
    float applyMetric(float x, float y, float z);

    default float applyMetric(Vector3f vector) {
        return this.applyMetric(vector.x, vector.y, vector.z);
    }
}
