package org.inksnow.core.impl.data.store.world.linear;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import lombok.extern.slf4j.Slf4j;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.util.ChunkPos;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("all")
public class LinearRegionFile implements AbstractRegionFile, AutoCloseable {

    private static final long SUPERBLOCK = -4323716122432332390L;
    private static final byte VERSION = 2;
    private static final int HEADER_SIZE = 32;
    private static final int FOOTER_SIZE = 8;
    private static final List<Byte> SUPPORTED_VERSIONS = Arrays.asList((byte) 1, (byte) 2);
    private static final LinearRegionFileFlusher linearRegionFileFlusher = new LinearRegionFileFlusher();
    private final byte[][] buffer = new byte[1024][];
    private final int[] bufferUncompressedSize = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private final LZ4Compressor compressor;
    private final LZ4FastDecompressor decompressor;
    private final int compressionLevel;
    private final AtomicBoolean markedToSave = new AtomicBoolean(false);
    public boolean closed = false;
    public Path path;


    public LinearRegionFile(Path file, int compression) throws IOException {
        this.path = file;
        this.compressionLevel = compression;
        this.compressor = LZ4Factory.fastestInstance().fastCompressor();
        this.decompressor = LZ4Factory.fastestInstance().fastDecompressor();

        File regionFile = new File(this.path.toString());

        Arrays.fill(this.bufferUncompressedSize, 0);

        if (!regionFile.canRead()) return;

        try (FileInputStream fileStream = new FileInputStream(regionFile);
             DataInputStream rawDataStream = new DataInputStream(fileStream)) {

            long superBlock = rawDataStream.readLong();
            if (superBlock != SUPERBLOCK)
                throw new RuntimeException("Invalid superblock: " + superBlock + " in " + file);

            byte version = rawDataStream.readByte();
            if (!SUPPORTED_VERSIONS.contains(version))
                throw new RuntimeException("Invalid version: " + version + " in " + file);

            // Skip newestTimestamp (Long) + Compression level (Byte) + Chunk count (Short): Unused.
            rawDataStream.skipBytes(11);

            int dataCount = rawDataStream.readInt();
            long fileLength = file.toFile().length();
            if (fileLength != HEADER_SIZE + dataCount + FOOTER_SIZE)
                throw new IOException("Invalid file length: " + this.path + " " + fileLength + " " + (HEADER_SIZE + dataCount + FOOTER_SIZE));

            rawDataStream.skipBytes(8); // Skip data hash (Long): Unused.

            byte[] rawCompressed = new byte[dataCount];
            rawDataStream.readFully(rawCompressed, 0, dataCount);

            superBlock = rawDataStream.readLong();
            if (superBlock != SUPERBLOCK)
                throw new IOException("Footer superblock invalid " + this.path);

            try (DataInputStream dataStream = new DataInputStream(new ZstdInputStream(new ByteArrayInputStream(rawCompressed)))) {

                int[] starts = new int[1024];
                for (int i = 0; i < 1024; i++) {
                    starts[i] = dataStream.readInt();
                    dataStream.skipBytes(4); // Skip timestamps (Int): Unused.
                }

                for (int i = 0; i < 1024; i++) {
                    if (starts[i] > 0) {
                        int size = starts[i];
                        byte[] b = new byte[size];
                        dataStream.readFully(b, 0, size);

                        int maxCompressedLength = this.compressor.maxCompressedLength(size);
                        byte[] compressed = new byte[maxCompressedLength];
                        int compressedLength = this.compressor.compress(b, 0, size, compressed, 0, maxCompressedLength);
                        b = new byte[compressedLength];
                        System.arraycopy(compressed, 0, b, 0, compressedLength);

                        this.buffer[i] = b;
                        this.bufferUncompressedSize[i] = size;
                    }
                }
            }
        }
    }

    private static int getChunkIndex(int x, int z) {
        return (x & 31) + ((z & 31) << 5);
    }

    private static int getTimestamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public void flush() throws IOException {
        if (isMarkedToSave()) flushWrapper(); // sync
    }

    private void markToSave() {
        linearRegionFileFlusher.scheduleSave(this);
        markedToSave.set(true);
    }

    public boolean isMarkedToSave() {
        return markedToSave.getAndSet(false);
    }

    public void flushWrapper() {
        try {
            save();
        } catch (IOException e) {
            logger.error("Failed to flush region file {}", path.toAbsolutePath(), e);
        }
    }

    private synchronized void save() throws IOException {
        long timestamp = getTimestamp();
        short chunkCount = 0;

        File tempFile = new File(path.toString() + ".tmp");

        try (FileOutputStream fileStream = new FileOutputStream(tempFile);
             ByteArrayOutputStream zstdByteArray = new ByteArrayOutputStream();
             ZstdOutputStream zstdStream = new ZstdOutputStream(zstdByteArray, this.compressionLevel);
             DataOutputStream zstdDataStream = new DataOutputStream(zstdStream);
             DataOutputStream dataStream = new DataOutputStream(fileStream)) {

            dataStream.writeLong(SUPERBLOCK);
            dataStream.writeByte(VERSION);
            dataStream.writeLong(timestamp);
            dataStream.writeByte(this.compressionLevel);

            ArrayList<byte[]> byteBuffers = new ArrayList<>();
            for (int i = 0; i < 1024; i++) {
                if (this.bufferUncompressedSize[i] != 0) {
                    chunkCount += 1;
                    byte[] content = new byte[bufferUncompressedSize[i]];
                    this.decompressor.decompress(buffer[i], 0, content, 0, bufferUncompressedSize[i]);

                    byteBuffers.add(content);
                } else byteBuffers.add(null);
            }
            for (int i = 0; i < 1024; i++) {
                zstdDataStream.writeInt(this.bufferUncompressedSize[i]); // Write uncompressed size
                zstdDataStream.writeInt(this.chunkTimestamps[i]); // Write timestamp
            }
            for (int i = 0; i < 1024; i++) {
                if (byteBuffers.get(i) != null)
                    zstdDataStream.write(byteBuffers.get(i), 0, byteBuffers.get(i).length);
            }
            zstdDataStream.close();

            dataStream.writeShort(chunkCount);

            byte[] compressed = zstdByteArray.toByteArray();

            dataStream.writeInt(compressed.length);
            dataStream.writeLong(0);

            dataStream.write(compressed, 0, compressed.length);
            dataStream.writeLong(SUPERBLOCK);

            dataStream.flush();
            fileStream.getFD().sync();
            fileStream.getChannel().force(true); // Ensure atomicity on Btrfs
        }
        Files.move(tempFile.toPath(), this.path, StandardCopyOption.REPLACE_EXISTING);
    }

    public synchronized void write(ChunkPos pos, ByteBuffer buffer) {
        try {
            byte[] b = toByteArray(new ByteArrayInputStream(buffer.array()));
            int uncompressedSize = b.length;

            int maxCompressedLength = this.compressor.maxCompressedLength(b.length);
            byte[] compressed = new byte[maxCompressedLength];
            int compressedLength = this.compressor.compress(b, 0, b.length, compressed, 0, maxCompressedLength);
            b = new byte[compressedLength];
            System.arraycopy(compressed, 0, b, 0, compressedLength);

            int index = getChunkIndex(pos.x, pos.z);
            this.buffer[index] = b;
            this.chunkTimestamps[index] = getTimestamp();
            this.bufferUncompressedSize[getChunkIndex(pos.x, pos.z)] = uncompressedSize;
        } catch (IOException e) {
            logger.error("Chunk write IOException {} {}", e, this.path);
        }
        markToSave();
    }

    @Override
    public DataOutputStream getChunkDataOutputStream(ChunkPos pos) {
        return new DataOutputStream(new BufferedOutputStream(new ChunkBuffer(pos)));
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] tempBuffer = new byte[4096];

        int length;
        while ((length = in.read(tempBuffer)) >= 0) {
            out.write(tempBuffer, 0, length);
        }

        return out.toByteArray();
    }

    @Override
    public synchronized @Nullable DataInputStream getChunkDataInputStream(ChunkPos pos) {
        if (this.bufferUncompressedSize[getChunkIndex(pos.x, pos.z)] != 0) {
            byte[] content = new byte[bufferUncompressedSize[getChunkIndex(pos.x, pos.z)]];
            this.decompressor.decompress(this.buffer[getChunkIndex(pos.x, pos.z)], 0, content, 0, bufferUncompressedSize[getChunkIndex(pos.x, pos.z)]);
            return new DataInputStream(new ByteArrayInputStream(content));
        }
        return null;
    }

    @Override
    public void clear(ChunkPos pos) {
        int i = getChunkIndex(pos.x, pos.z);
        this.buffer[i] = null;
        this.bufferUncompressedSize[i] = 0;
        this.chunkTimestamps[i] = getTimestamp();
        markToSave();
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        flush(); // sync
    }

    private class ChunkBuffer extends ByteArrayOutputStream {
        private final ChunkPos pos;

        public ChunkBuffer(ChunkPos chunkcoordintpair) {
            super();
            this.pos = chunkcoordintpair;
        }

        public void close() {
            ByteBuffer bytebuffer = ByteBuffer.wrap(this.buf, 0, this.count);
            LinearRegionFile.this.write(this.pos, bytebuffer);
        }
    }
}
