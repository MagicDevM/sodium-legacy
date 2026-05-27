package net.caffeinemc.mods.sodium.client.render.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.caffeinemc.mods.sodium.client.render.vertex.VertexFormatDescriptionImpl;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatRegistry;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;

import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class VertexFormatRegistryImpl implements VertexFormatRegistry {
    private static final int ABSENT_INDEX = -1;

    private final Map<VertexFormat, VertexFormatDescriptionImpl> descriptions = new Reference2ReferenceOpenHashMap<>();
    private final Reference2IntMap<VertexFormat> descriptionIds = new Reference2IntOpenHashMap<>();
    private final StampedLock lock = new StampedLock();

    public VertexFormatRegistryImpl() {
        this.descriptionIds.defaultReturnValue(ABSENT_INDEX);
    }

    @Override
    public VertexFormatDescription get(VertexFormat format) {
        VertexFormatDescription desc = this.findExisting(format);

        if (desc == null) {
            desc = this.create(format);
        }

        return desc;
    }

    private VertexFormatDescription findExisting(VertexFormat format) {
        var stamp = this.lock.readLock();

        try {
            return this.descriptions.get(format);
        } finally {
            this.lock.unlockRead(stamp);
        }
    }

    private VertexFormatDescription create(VertexFormat format) {
        var stamp = this.lock.writeLock();

        var id = this.descriptions.size();
        var desc = new VertexFormatDescriptionImpl(format, id);

        try {
            this.descriptions.put(format, desc);
        } finally {
            this.lock.unlockWrite(stamp);
        }

        return desc;
    }

    @Override
    public int allocateGlobalId(VertexFormat format) {
        int id;

        {
            var stamp = this.lock.readLock();

            try {
                id = this.descriptionIds.getInt(format);
            } finally {
                this.lock.unlockRead(stamp);
            }
        }

        if (id == ABSENT_INDEX) {
            var stamp = this.lock.writeLock();

            try {
                this.descriptionIds.put(format, id = this.descriptionIds.size());
            } finally {
                this.lock.unlockWrite(stamp);
            }
        }

        return id;
    }
}
