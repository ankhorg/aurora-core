package org.inksnow.core.data.holder;

import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.persistence.DataContainerHolder;
import org.inksnow.core.position.BlockPosition;
import org.inksnow.core.position.ChunkPosition;

public interface WorldDataHolder extends DataHolder.Mutable, DataContainerHolder.Mutable {
    /**
     * Get the data holder of a chunk.
     *
     * @param position the position
     * @return the data holder
     */
    ChunkDataHolder chunk(ChunkPosition position);

    /**
     * Get the data holder of a block.
     *
     * @param position the position
     * @return the data holder
     */
    BlockDataHolder block(BlockPosition position);
}
