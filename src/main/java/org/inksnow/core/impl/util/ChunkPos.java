package org.inksnow.core.impl.util;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;

@RequiredArgsConstructor
@EqualsAndHashCode
@SuppressWarnings("MemberName")
public class ChunkPos {
    public final int x;
    public final int z;

    public ChunkPos(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }
}
