package org.inksnow.core.impl.data.store.world.linear;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings("all")
public class LinearRegionFileFlusher {
    private static final int FLUSH_THREADS = 1;
    private static final int FLUSH_FREQUENCY = 10;

    private final Queue<LinearRegionFile> savingQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setNameFormat("linear-flush-scheduler")
                    .build()
    );
    private final ExecutorService executor = Executors.newFixedThreadPool(
            FLUSH_THREADS,
            new ThreadFactoryBuilder()
                    .setNameFormat("linear-flusher-%d")
                    .build()
    );

    public LinearRegionFileFlusher() {
        logger.info("Using {} threads for linear region flushing.", FLUSH_THREADS);
        scheduler.scheduleAtFixedRate(this::pollAndFlush, 0L, FLUSH_FREQUENCY, TimeUnit.SECONDS);
    }

    public void scheduleSave(LinearRegionFile regionFile) {
        if (savingQueue.contains(regionFile)) return;
        savingQueue.add(regionFile);
    }

    private void pollAndFlush() {
        while (!savingQueue.isEmpty()) {
            LinearRegionFile regionFile = savingQueue.poll();
            if (!regionFile.closed && regionFile.isMarkedToSave())
                executor.execute(regionFile::flushWrapper);
        }
    }

    public void shutdown() {
        executor.shutdown();
        scheduler.shutdown();
    }
}
