package org.inksnow.core.impl.data.holder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.data.persistence.DataContainerHolder;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.ref.nbt.RefNbtAccounter;
import org.inksnow.core.impl.ref.nbt.RefNbtIo;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class AuroraNbtFileDataHolder implements DataCompoundHolder, DataContainerHolder.Mutable, Closeable, AutoCloseable {
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
            if (CraftBukkitVersion.v1_20_R1.isSupport()) {
                return RefNbtIo.readCompressed1(persistentDataPath, new RefNbtAccounter(104857600L, 512));
            } else {
                return RefNbtIo.readCompressed0(persistentDataPath.toFile());
            }
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

        if (CraftBukkitVersion.v1_20_R1.isSupport()) {
            RefNbtIo.writeCompressed1(compound, persistentDataPath);
        } else {
            RefNbtIo.writeCompressed0(compound, persistentDataPath.toFile());
        }
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            flush();
        }
    }
}
