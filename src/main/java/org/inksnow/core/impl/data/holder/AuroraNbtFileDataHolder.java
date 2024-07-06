package org.inksnow.core.impl.data.holder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.ref.nbt.RefNbtIo;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class AuroraNbtFileDataHolder implements DataCompoundHolder, Closeable, AutoCloseable {
    protected final Path persistentDataPath;
    protected RefNbtTagCompound compound;
    private boolean closed;

    @SneakyThrows
    protected AuroraNbtFileDataHolder(Path persistentDataPath) {
        this.persistentDataPath = persistentDataPath;
        this.compound = loadImpl();
    }

    private RefNbtTagCompound loadImpl() throws IOException {
        if (Files.exists(persistentDataPath)) {
            return RefNbtIo.readCompressed(persistentDataPath.toFile());
        } else {
            return new RefNbtTagCompound();
        }
    }

    @Override
    public RefNbtTagCompound data$getCompound() {
        return this.compound;
    }

    @Override
    public void data$setCompound(RefNbtTagCompound nbt) {
        this.compound = nbt;
    }

    @SneakyThrows
    protected void flush() {
        logger.debug("Flushing nbt file data: {}", persistentDataPath);
        Files.createDirectories(persistentDataPath.getParent());
        RefNbtIo.writeCompressed(compound, persistentDataPath.toFile());
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            flush();
        }
    }
}
