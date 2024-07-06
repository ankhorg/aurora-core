package org.inksnow.core.impl.data.store.world.linear;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.util.ChunkPos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public interface AbstractRegionFile {

    Path getPath();

    @Nullable
    DataInputStream getChunkDataInputStream(ChunkPos pos) throws IOException;

    DataOutputStream getChunkDataOutputStream(ChunkPos pos) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;

    void clear(ChunkPos pos) throws IOException;
}
