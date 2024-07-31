package org.inksnow.core.data.holder;

import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.Aurora;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.position.BlockPosition;

public interface ChunkDataHolder extends DataHolder.Mutable {
    BlockDataHolder block(BlockPosition position);
}
