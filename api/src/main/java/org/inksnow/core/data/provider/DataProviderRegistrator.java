package org.inksnow.core.data.provider;

import org.inksnow.core.Aurora;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DataProviderRegistrator {

    Supplier<Factory> factory = Aurora.getFactoryLazy(DataProviderRegistrator.Factory.class);

    static Factory factory() {
        return factory.get();
    }

    static DataProviderRegistrator create() {
        return factory.get().create();
    }

    DataProviderRegistrator newDataStore(Class<? extends DataHolder>... dataHolders);

    @SuppressWarnings({"rawtypes", "unchecked"})
    void pluginDataStore(ResourcePath datastoreKey, Class dataHolder, Key<? extends Value<?>>... dataKeys);

    <K, V extends Value<K>> DataProviderRegistrator dataStore(
            Key<V> key,
            BiConsumer<DataView, K> serializer,
            Function<DataView, Optional<K>> deserializer
    );

    <H extends DataHolder, K, V extends Value<K>> void registerDataStoreDelegatingProvider(Key<V> key, Type typeToken);

    <T> Mutable<T> asMutable(Class<T> target);

    <T> Immutable<T> asImmutable(Class<T> target);

    void buildAndRegister();

    interface Immutable<T> extends DataProviderRegistrator {

        <K> ImmutableRegistration<T, K, ?> create(Supplier<? extends Key<? extends Value<K>>> suppliedKey);

        <K> ImmutableRegistration<T, K, ?> create(Key<? extends Value<K>> key);
    }

    interface Mutable<T> extends DataProviderRegistrator {

        <K> MutableRegistration<T, K, ?> create(Supplier<? extends Key<? extends Value<K>>> suppliedKey);

        <K> MutableRegistration<T, K, ?> create(Key<? extends Value<K>> key);
    }


    interface MutableRegistration<H, E, R extends MutableRegistration<H, E, R>> {

        <NE> MutableRegistration<H, NE, ?> create(Key<? extends Value<NE>> key);

        <NT> Mutable<NT> asMutable(Class<NT> target);

        <NT> Immutable<NT> asImmutable(Class<NT> target);

        R constructValue(BiFunction<H, E, Value<E>> constructValue);

        R get(Function<H, E> get);

        R set(BiConsumer<H, E> set);

        R setAnd(BiFunction<H, E, Boolean> setAnd);

        R delete(Consumer<H> delete);

        R deleteAnd(Function<H, Boolean> deleteAnd);

        R deleteAndGet(Function<H, DataTransactionResult> deleteAndGet);

        R resetOnDelete(E value);

        R resetOnDelete(Supplier<E> resetOnDeleteTo);

        R resetOnDelete(Function<H, E> resetOnDeleteTo);

        R setAndGet(BiFunction<H, E, DataTransactionResult> setAndGet);

        R supports(Function<H, Boolean> supports);

        DataProvider<?, ?> build(Class<H> target);
    }

    interface ImmutableRegistration<H, E, R extends ImmutableRegistration<H, E, R>> {
        <NE> ImmutableRegistration<H, NE, ?> create(Key<? extends Value<NE>> key);

        <NT> Mutable<NT> asMutable(Class<NT> target);

        <NT> Immutable<NT> asImmutable(Class<NT> target);

        R constructValue(final BiFunction<H, E, Value<E>> constructValue);

        R get(final Function<H, E> get);

        R set(final BiFunction<H, E, H> set);

        R supports(final Function<H, Boolean> supports);

        DataProvider<?, ?> build(final Class<H> target);
    }

    interface Factory {
        DataProviderRegistrator create();
    }
}
