package org.inksnow.core;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.spi.ServiceApi;
import org.inksnow.core.util.Builder;

/**
 * The main static entry point of the Aurora API.
 */
public final class Aurora {
    /**
     * The API instance.
     */
    private static @MonotonicNonNull AuroraApi api;

    private Aurora() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets the Aurora API.
     *
     * @return the Aurora API.
     * @throws IllegalStateException if the API has not been initialized.
     */
    @SneakyThrows
    @SuppressWarnings("argument") // reflection obj could be null, but checker framework doesn't know
    public static AuroraApi api() {
        @Nullable AuroraApi api = Aurora.api;
        if (api == null) {
            api = (AuroraApi) Class.forName("org.inksnow.core.loader.AuroraCorePlugin")
                .getMethod("instance")
                .invoke(null);
            if (api == null) {
                throw new IllegalStateException("Aurora API failed to initialize");
            }
            Aurora.api = api;
        }
        return api;
    }

    /**
     * Returns the service API.
     *
     * @return the service API
     */
    public static ServiceApi service() {
        return api().service();
    }

    /**
     * Creates a builder.
     *
     * @param clazz the builder class
     * @param <B> the type of the builder
     * @return the builder
     */
    public static <B extends Builder<?, ?>> B createBuilder(Class<B> clazz) {
        return api().createBuilder(clazz);
    }

    /**
     * Gets the factory with the specified class.
     *
     * @param clazz the factory class
     * @param <T> the type of the factory
     * @return the factory
     */
    public static <T> T getFactory(Class<T> clazz) {
        return api().getFactory(clazz);
    }

}
