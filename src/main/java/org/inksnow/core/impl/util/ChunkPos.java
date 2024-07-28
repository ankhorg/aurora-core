package org.inksnow.core.impl.util;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;

@RequiredArgsConstructor
@SuppressWarnings("MemberName")
public final class ChunkPos {
    public final int x;
    public final int z;

    public ChunkPos(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ChunkPos)) {
            return false;
        }
        ChunkPos other = (ChunkPos) obj;
        return x == other.x && z == other.z;
    }

    @Override
    public int hashCode() {
        return (x << 4) | z;
    }
}
