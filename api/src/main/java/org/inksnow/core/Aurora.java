package org.inksnow.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataApi;
import org.inksnow.core.spi.ServiceApi;
import org.inksnow.core.util.Builder;

import java.util.concurrent.atomic.AtomicReference;

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
     * Initializes the Aurora API.
     *
     * @param api the API instance.
     * @throws IllegalStateException if the API has already been initialized.
     */
    public static void api(AuroraApi api) throws IllegalStateException {
        Preconditions.checkNotNull(api, "api");
        Preconditions.checkState(Aurora.api == null || Aurora.api == api, "Aurora has already been initialized");

        Aurora.api = api;
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
            api(api);
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

    public static DataApi data() {
        return api().data();
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

    /**
     * Gets a lazy factory with the specified class.
     *
     * @param clazz the factory class
     * @return the lazy factory
     * @param <T> the type of the factory
     */
    public static <T> Supplier<T> getFactoryLazy(Class<T> clazz) {
        return new Supplier<T>() {
            private final AtomicReference<@Nullable T> reference = new AtomicReference<>();

            @Override
            public T get() {
                T instance = reference.get();
                if (instance == null) {
                    synchronized (this) {
                        instance = reference.get();
                        if (instance == null) {
                            instance = getFactory(clazz);
                            reference.set(instance);
                        }
                    }
                }
                return instance;
            }
        };
    }
}
