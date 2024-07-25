package org.inksnow.core.impl.data.provider;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.DataRegistration;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.ImmutableDataProviderBuilder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.provider.MutableDataProviderBuilder;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.bridge.data.DataContainerHolder;
import org.inksnow.core.impl.data.AuroraDataManager;
import org.inksnow.core.impl.data.AuroraDataRegistration;
import org.inksnow.core.impl.data.AuroraDataRegistrationBuilder;
import org.inksnow.core.impl.data.persistence.datastore.AuroraDataStoreBuilder;
import org.inksnow.core.impl.util.CopyHelper;
import org.inksnow.core.impl.util.TypeTokenUtil;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataProviderRegistrator {
    private static final Class<DataContainerHolder.Mutable> MUTABLE = DataContainerHolder.Mutable.class;
    private static final Class<DataContainerHolder.Immutable> IMMUTABLE = DataContainerHolder.Immutable.class;

    AuroraDataRegistrationBuilder registrationBuilder;
    AuroraDataStoreBuilder dataStoreBuilder;

    public DataProviderRegistrator() {
        this.registrationBuilder = (AuroraDataRegistrationBuilder) DataRegistration.builder();
        this.dataStoreBuilder = (AuroraDataStoreBuilder) DataStore.builder().vanillaData();
    }

    public DataProviderRegistrator(final AuroraDataRegistrationBuilder registrationBuilder) {
        this.registrationBuilder = registrationBuilder;
    }

    @SafeVarargs
    public final DataProviderRegistrator newDataStore(Class<? extends DataHolder>... dataHolders) {
        if (!this.dataStoreBuilder.isEmpty()) {
            this.registrationBuilder.store(this.dataStoreBuilder.build());
        }
        this.dataStoreBuilder.reset();
        this.dataStoreBuilder.holder(dataHolders);
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void auroraDataStore(final ResourcePath datastoreKey, final Class dataHolder, final Key<? extends Value<?>>... dataKeys) {
        final AuroraDataStoreBuilder builder = ((AuroraDataStoreBuilder) DataStore.builder()).pluginData(datastoreKey);
        builder.holder(dataHolder);
        for (Key dataKey : dataKeys) {
            builder.key(dataKey, dataKey.resourcePath().asDataQuery());
        }
        AuroraDataManager.getDatastoreRegistry().register(builder.build(), Arrays.asList(dataKeys));
    }

    public <K, V extends Value<K>> DataProviderRegistrator dataStore(
        final Key<V> key,
        final BiConsumer<DataView, K> serializer,
        final Function<DataView, Optional<K>> deserializer
    ) {
        this.dataStoreBuilder.key(key, serializer, deserializer);
        this.dataStoreBuilder.getDataHolderTypes().forEach(typeToken -> this.registerDataStoreDelegatingProvider(key, typeToken));
        return this;
    }

    public <H extends DataHolder, K, V extends Value<K>> void registerDataStoreDelegatingProvider(final Key<V> key, final Type typeToken) {
        // Create dataprovider for mutable and immutable DataContainerHolders
        if (GenericTypeReflector.isSuperType(DataProviderRegistrator.MUTABLE, typeToken)) {
            this.asMutable(GenericTypeReflector.erase(typeToken))
                    .create(key)
                    .get(holder -> {
                        final DataContainer dataContainer = ((DataContainerHolder) holder).data$getDataContainer();
                        return AuroraDataManager.getDatastoreRegistry()
                            .getDataStore(key, typeToken)
                            .deserialize(dataContainer)
                            .get(key)
                            .orElse(null);
                    })
                    .set((holder, v) -> {
                        final DataContainer dataContainer = ((DataContainerHolder) holder).data$getDataContainer();
                        final DataManipulator.Mutable manipulator = DataManipulator.mutableOf();
                        manipulator.set(key, v);
                        AuroraDataManager.getDatastoreRegistry().getDataStore(key, typeToken).serialize(manipulator, dataContainer);
                        ((DataContainerHolder.Mutable) holder).data$setDataContainer(dataContainer);
                    });
        } else if (GenericTypeReflector.isSuperType(DataProviderRegistrator.IMMUTABLE, typeToken)) {
            this.asImmutable((Class<? super H>) GenericTypeReflector.erase(typeToken))
                    .create(key)
                    .get(holder -> {
                        final DataContainer dataContainer = ((DataContainerHolder) holder).data$getDataContainer();
                        return AuroraDataManager.getDatastoreRegistry().getDataStore(key, typeToken).deserialize(dataContainer).get(key).orElse(null);
                    })
                    .set((holder, v) -> {
                        final DataContainer dataContainer = ((DataContainerHolder) holder).data$getDataContainer();
                        final DataManipulator.Mutable manipulator = DataManipulator.mutableOf();
                        manipulator.set(key, v);
                        AuroraDataManager.getDatastoreRegistry().getDataStore(key, typeToken).serialize(manipulator, dataContainer);
                        return (H) ((DataContainerHolder.Immutable) holder).data$withDataContainer(dataContainer);
                    });
        }
    }

    /**
     * Creates a new {@link MutableRegistrator}
     * @return The registrator
     */
    public <T> MutableRegistrator<T> asMutable(final Class<T> target) {
        return new MutableRegistrator<>(this.registrationBuilder, target);
    }

    /**
     * Creates a new {@link ImmutableRegistrator}
     * @return The registrator
     */
    public <T> ImmutableRegistrator<T> asImmutable(final Class<T> target) {
        return new ImmutableRegistrator<>(this.registrationBuilder, target);
    }

    public void buildAndRegister() {
        if (!this.dataStoreBuilder.isEmpty()) {
            this.registrationBuilder.store(this.dataStoreBuilder.build());
        }
        AuroraDataManager.INSTANCE.registerDataRegistration((AuroraDataRegistration) this.registrationBuilder.build());
    }


    public static final class MutableRegistrator<T> extends DataProviderRegistrator {

        private final Class<T> target;

        public MutableRegistrator(final AuroraDataRegistrationBuilder builder, final Class<T> target) {
            super(builder);
            this.target = target;
        }

        /**
         * Creates a new {@link ImmutableRegistration} and registers it
         * @param suppliedKey The key supplier
         * @param <K> The key type
         * @return The registration
         */
        public <K> MutableRegistration<T, K> create(final Supplier<? extends Key<? extends Value<K>>> suppliedKey) {
            return this.create(suppliedKey.get());
        }

        /**
         * Creates a new {@link ImmutableRegistration} and registers it
         * @param key The key
         * @param <K> The key type
         * @return The registration
         */
        public <K> MutableRegistration<T, K> create(final Key<? extends Value<K>> key) {
            final MutableRegistration<T, K> registration = new MutableRegistration<>(this, key);
            this.register(registration);
            return registration;
        }

        @SuppressWarnings({"unchecked", "UnstableApiUsage"})
        protected <K, V extends Value<K>> MutableRegistrator<T> register(final MutableRegistration<T, K> registration) {
            final DataProvider<?, ?> provider = registration.build(this.target);
            this.registrationBuilder.dataKey(provider.key()).provider(provider);
            return this;
        }
    }

    public static final class ImmutableRegistrator<T> extends DataProviderRegistrator {

        private final Class<T> target;

        public ImmutableRegistrator(final AuroraDataRegistrationBuilder builder, final Class<T> target) {
            super(builder);
            this.target = target;
        }

        /**
         * Creates a new {@link ImmutableRegistration} and registers it
         * @param suppliedKey The key supplier
         * @param <K> The key type
         * @return The registration
         */
        public <K> ImmutableRegistration<T, K> create(final Supplier<? extends Key<? extends Value<K>>> suppliedKey) {
            return this.create(suppliedKey.get());
        }

        /**
         * Creates a new {@link ImmutableRegistration} and registers it
         * @param key The key
         * @param <K> The key type
         * @return The registration
         */
        public <K> ImmutableRegistration<T, K> create(final Key<? extends Value<K>> key) {
            final ImmutableRegistration<T, K> registration = new ImmutableRegistration<>(this, key);
            this.register(registration);
            return registration;
        }

        @SuppressWarnings({"unchecked", "UnstableApiUsage"})
        protected <K, V> ImmutableRegistrator<T> register(final ImmutableRegistration<T, K> registration) {
            final DataProvider<?, ?> provider = registration.build(this.target);
            this.registrationBuilder.dataKey(provider.key()).provider(provider);
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    private static class MutableRegistrationBase<H, E, R extends MutableRegistrationBase<H, E, R>> {

        final Key<? extends Value<E>> key;
        private @Nullable BiFunction<H, E, Value<E>> constructValue;
        private @Nullable Function<H, @Nullable E> get;
        private @Nullable BiFunction<H, E, Boolean> setAnd;
        private @Nullable BiConsumer<H, E> set;
        private @Nullable Function<H, Boolean> deleteAnd;
        private @Nullable Consumer<H> delete;
        private @Nullable Function<H, DataTransactionResult> deleteAndGet;
        private @Nullable Function<H, E> resetOnDelete;
        private @Nullable BiFunction<H, E, DataTransactionResult> setAndGet;
        private @Nullable Function<H, Boolean> supports;

        public MutableRegistrationBase(Key<? extends Value<E>> key) {
            this.key = key;
        }

        public R constructValue(final BiFunction<H, E, Value<E>> constructValue) {
            this.constructValue = constructValue;
            return (R) this;
        }

        public R get(final Function<H, @Nullable E> get) {
            this.get = get;
            return (R) this;
        }

        public R set(final BiConsumer<H, E> set) {
            this.set = set;
            return (R) this;
        }

        public R setAnd(final BiFunction<H, E, Boolean> setAnd) {
            this.setAnd = setAnd;
            return (R) this;
        }

        public R delete(final Consumer<H> delete) {
            this.delete = delete;
            return (R) this;
        }

        public R deleteAnd(final Function<H, Boolean> deleteAnd) {
            this.deleteAnd = deleteAnd;
            return (R) this;
        }

        public R deleteAndGet(final Function<H, DataTransactionResult> deleteAndGet) {
            this.deleteAndGet = deleteAndGet;
            return (R) this;
        }

        public R resetOnDelete(final E value) {
            return this.resetOnDelete(CopyHelper.createSupplier(value));
        }

        public R resetOnDelete(final Supplier<E> resetOnDeleteTo) {
            return this.resetOnDelete(h -> resetOnDeleteTo.get());
        }

        public R resetOnDelete(final Function<H, E> resetOnDeleteTo) {
            this.resetOnDelete = resetOnDeleteTo;
            return (R) this;
        }

        public R setAndGet(final BiFunction<H, E, DataTransactionResult> setAndGet) {
            this.setAndGet = setAndGet;
            return (R) this;
        }

        public R supports(final Function<H, Boolean> supports) {
            this.supports = supports;
            return (R) this;
        }

        public DataProvider<?, ?> build(Class<H> target) {
            final MutableRegistrationBase<H, E, R> registration = this;
            return new GenericMutableDataProvider<H, E>(registration.key, target) {
                final boolean isBooleanKey = registration.key.elementType() == Boolean.class;

                @Override
                protected Value<E> constructValue(final H dataHolder, final E element) {
                    if (registration.constructValue != null) {
                        return registration.constructValue.apply(dataHolder, element);
                    }
                    return super.constructValue(dataHolder, element);
                }

                @Override
                protected Optional<E> getFrom(final H dataHolder) {
                    if (registration.get == null) {
                        return Optional.empty();
                    }
                    if (this.isBooleanKey) {
                        return (Optional<E>) Optional.ofNullable((Boolean) registration.get.apply(dataHolder));
                    }
                    return Optional.ofNullable(registration.get.apply(dataHolder));
                }

                @Override
                protected boolean set(final H dataHolder, final E value) {
                    if (registration.setAnd != null) {
                        return registration.setAnd.apply(dataHolder, value);
                    }
                    if (registration.set != null) {
                        registration.set.accept(dataHolder, value);
                        return true;
                    }
                    return super.set(dataHolder, value);
                }

                @Override
                protected boolean delete(final H dataHolder) {
                    if (registration.deleteAnd != null) {
                        return registration.deleteAnd.apply(dataHolder);
                    }
                    if (registration.delete != null) {
                        registration.delete.accept(dataHolder);
                        return true;
                    }
                    if (registration.resetOnDelete != null) {
                        return this.set(dataHolder, registration.resetOnDelete.apply(dataHolder));
                    }
                    return super.delete(dataHolder);
                }

                @Override
                protected DataTransactionResult setAndGetResult(final H dataHolder, final E value) {
                    if (registration.setAndGet != null) {
                        return registration.setAndGet.apply(dataHolder, value);
                    }
                    return super.setAndGetResult(dataHolder, value);
                }

                @Override
                protected DataTransactionResult deleteAndGetResult(final H dataHolder) {
                    if (registration.deleteAndGet != null) {
                        return registration.deleteAndGet.apply(dataHolder);
                    }
                    if (registration.resetOnDelete != null) {
                        return this.setAndGetResult(dataHolder, registration.resetOnDelete.apply(dataHolder));
                    }
                    return super.deleteAndGetResult(dataHolder);
                }

                @Override
                protected boolean supports(final H dataHolder) {
                    if (registration.supports != null) {
                        return registration.supports.apply(dataHolder);
                    }
                    return super.supports(dataHolder);
                }
            };


        }

    }

    public static final class MutableRegistration<H, E> extends MutableRegistrationBase<H, E, MutableRegistration<H, E>> {

        private final MutableRegistrator<H> registrator;

        private MutableRegistration(final MutableRegistrator<H> registrator, final Key<? extends Value<E>> key) {
            super(key);
            this.registrator = registrator;
        }

        /* public <NE> MutableRegistration<H, NE> create(final DefaultedRegistryReference<? extends Key<? extends Value<NE>>> suppliedKey) {
            return this.create(suppliedKey.get());
        } */

        public <NE> MutableRegistration<H, NE> create(final Key<? extends Value<NE>> key) {
            final MutableRegistration<H, NE> registration = new MutableRegistration<>(this.registrator, key);
            this.registrator.register(registration);
            return registration;
        }

        /**
         * Creates a new {@link MutableRegistrator}
         * @return The registrator
         */
        public <NT> MutableRegistrator<NT> asMutable(final Class<NT> target) {
            return new MutableRegistrator<>(this.registrator.registrationBuilder, target);
        }

        /**
         * Creates a new {@link ImmutableRegistrator}
         * @return The registrator
         */
        public <NT> ImmutableRegistrator<NT> asImmutable(final Class<NT> target) {
            return new ImmutableRegistrator<>(this.registrator.registrationBuilder, target);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ImmutableRegistrationBase<H, E, R extends ImmutableRegistrationBase<H, E, R>> {
        private final Key<? extends Value<E>> key;
        private @Nullable BiFunction<H, E, Value<E>> constructValue;
        private @Nullable Function<H, E> get;
        private @Nullable BiFunction<H, E, H> set;
        private @Nullable Function<H, Boolean> supports;

        public ImmutableRegistrationBase(Key<? extends Value<E>> key) {
            this.key = key;
        }

        public R constructValue(final BiFunction<H, E, Value<E>> constructValue) {
            this.constructValue = constructValue;
            return (R) this;
        }

        public R get(final Function<H, E> get) {
            this.get = get;
            return (R) this;
        }

        public R set(final BiFunction<H, E, H> set) {
            this.set = set;
            return (R) this;
        }

        public R supports(final Function<H, Boolean> supports) {
            this.supports = supports;
            return (R) this;
        }

        public DataProvider<?, ?> build(final Class<H> target) {
            final ImmutableRegistrationBase<H, E, R> registration = this;
            return new GenericImmutableDataProvider<H, E>(registration.key, target) {
                final boolean isBooleanKey = GenericTypeReflector.erase(registration.key.elementType())== Boolean.class;

                @Override
                protected Value<E> constructValue(final H dataHolder, final E element) {
                    if (registration.constructValue != null) {
                        return registration.constructValue.apply(dataHolder, element);
                    }
                    return super.constructValue(dataHolder, element);
                }

                @Override
                protected Optional<E> getFrom(final H dataHolder) {
                    if (registration.get == null) {
                        return Optional.empty();
                    }
                    if (this.isBooleanKey) {
                        return (Optional<E>) Optional.ofNullable((Boolean) registration.get.apply(dataHolder));
                    }
                    return Optional.ofNullable(registration.get.apply(dataHolder));
                }

                @Override
                protected Optional<H> set(final H dataHolder, final E value) {
                    if (registration.set == null) {
                        return Optional.empty();
                    }
                    return Optional.ofNullable(registration.set.apply(dataHolder, value));
                }

                @Override
                protected boolean supports(final H dataHolder) {
                    if (registration.supports != null) {
                        return registration.supports.apply(dataHolder);
                    }
                    return super.supports(dataHolder);
                }
            };

        }

    }

    public static final class ImmutableRegistration<H, E> extends ImmutableRegistrationBase<H, E, ImmutableRegistration<H, E>> {

        private final ImmutableRegistrator<H> registrator;

        private ImmutableRegistration(final ImmutableRegistrator<H> registrator, final Key<? extends Value<E>> key) {
            super(key);
            this.registrator = registrator;
        }

        /* public <NE> ImmutableRegistration<H, NE> create(final DefaultedRegistryReference<? extends Key<? extends Value<NE>>> suppliedKey) {
            return this.create(suppliedKey.get());
        } */

        public <NE> ImmutableRegistration<H, NE> create(final Key<? extends Value<NE>> key) {
            final ImmutableRegistration<H, NE> registration = new ImmutableRegistration<>(this.registrator, key);
            this.registrator.register(registration);
            return registration;
        }

        /**
         * Creates a new {@link MutableRegistrator}
         * @return The registrator
         */
        public <NT> MutableRegistrator<NT> asMutable(final Class<NT> target) {
            return new MutableRegistrator<>(this.registrator.registrationBuilder, target);
        }

        /**
         * Creates a new {@link ImmutableRegistrator}
         * @return The registrator
         */
        public <NT> ImmutableRegistrator<NT> asImmutable(final Class<NT> target) {
            return new ImmutableRegistrator<>(this.registrator.registrationBuilder, target);
        }
    }

    public static class SpongeImmutableDataProviderBuilder<H extends DataHolder, V extends Value<E>, E, R extends ImmutableRegistrationBase<H, E, R>> implements ImmutableDataProviderBuilder<H, V, E> {

        private ImmutableRegistrationBase<H, E, R> registration;
        private Type holder;

        @Override
        public <NV extends Value<NE>, NE> ImmutableDataProviderBuilder<H, NV, NE> key(Key<NV> key) {
            this.registration = new ImmutableRegistrationBase(key);
            return (SpongeImmutableDataProviderBuilder) this;
        }

        @Override
        public <NH extends H> ImmutableDataProviderBuilder<NH, V, E> dataHolder(TypeToken<NH> holder) {
            this.holder = holder.getType();
            return (SpongeImmutableDataProviderBuilder) this;
        }

        @Override
        public <NH extends H> ImmutableDataProviderBuilder<NH, V, E> dataHolder(final Class<NH> holder) {
            this.holder = TypeTokenUtil.requireCompleteGenerics(holder);
            return (SpongeImmutableDataProviderBuilder) this;
        }

        @Override
        public ImmutableDataProviderBuilder<H, V, E> get(Function<H, E> get) {
            this.registration.get(get);
            return this;
        }

        @Override
        public ImmutableDataProviderBuilder<H, V, E> set(BiFunction<H, E, H> set) {
            this.registration.set(set);
            return this;
        }

        @Override
        public ImmutableDataProviderBuilder<H, V, E> supports(Function<H, Boolean> supports) {
            this.registration.supports(supports);
            return this;
        }

        @Override
        public ImmutableDataProviderBuilder<H, V, E> reset() {
            this.registration = null;
            return this;
        }

        @Override
        public DataProvider<? extends Value<E>, E> build() {
            return this.registration.build((Class) GenericTypeReflector.erase(this.holder));
        }
    }

    public static class SpongeMutableDataProviderBuilder<H extends DataHolder.Mutable, V extends Value<E>, E, R extends MutableRegistrationBase<H, E, R>> implements MutableDataProviderBuilder<H, V, E> {

        private MutableRegistrationBase<H, E, R> registration;
        private Type holder;

        @Override
        public <NV extends Value<NE>, NE> MutableDataProviderBuilder<H, NV, NE> key(Key<NV> key) {
            this.registration = new MutableRegistrationBase(key);
            return (SpongeMutableDataProviderBuilder) this;
        }

        @Override
        public <NH extends H> MutableDataProviderBuilder<NH, V, E> dataHolder(final TypeToken<NH> holder) {
            this.holder = holder.getType();
            return (SpongeMutableDataProviderBuilder) this;
        }

        @Override
        public <NH extends H> MutableDataProviderBuilder<NH, V, E> dataHolder(final Class<NH> holder) {
            this.holder = TypeTokenUtil.requireCompleteGenerics(holder);
            return (SpongeMutableDataProviderBuilder) this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> get(Function<H, E> get) {
            this.registration.get(get);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> set(BiConsumer<H, E> set) {
            this.registration.set(set);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> setAnd(BiFunction<H, E, Boolean> setAnd) {
            this.registration.setAnd(setAnd);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> delete(Consumer<H> delete) {
            this.registration.delete(delete);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> deleteAnd(Function<H, Boolean> delete) {
            this.registration.deleteAnd(delete);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> deleteAndGet(Function<H, DataTransactionResult> delete) {
            this.registration.deleteAndGet(delete);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> resetOnDelete(Supplier<E> resetOnDeleteTo) {
            this.registration.resetOnDelete(resetOnDeleteTo);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> resetOnDelete(Function<H, E> resetOnDeleteTo) {
            this.registration.resetOnDelete(resetOnDeleteTo);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> setAndGet(BiFunction<H, E, DataTransactionResult> setAndGet) {
            this.registration.setAndGet(setAndGet);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> supports(Function<H, Boolean> supports) {
            this.registration.supports(supports);
            return this;
        }

        @Override
        public MutableDataProviderBuilder<H, V, E> reset() {
            this.registration = null;
            return this;
        }

        @Override
        public DataProvider<V, E> build() {
            return this.registration.build((Class) GenericTypeReflector.erase(this.holder));
        }
    }
}
