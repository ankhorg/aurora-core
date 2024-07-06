package org.inksnow.core.impl.data.holder;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface AuroraDataHolder extends DataHolder {
    default <V extends Value<E>, E> DataProvider<V, E> impl$getProviderFor(Key<V> key, DataHolder dataHolder) {
        Preconditions.checkArgument(key != null, "key");
        throw new UnsupportedOperationException();
        // return SpongeDataManager.getProviderRegistry().getProvider(key, dataHolder.getClass());
    }

    default List<DataHolder> impl$delegateDataHolder() {
        return Collections.singletonList(this);
    }

    default Collection<DataProvider<?, ?>> impl$getAllProviders(final DataHolder dataHolder) {
        throw new UnsupportedOperationException();
        // return SpongeDataManager.getProviderRegistry().getAllProviders(dataHolder.getClass());
    }

    default <T, E, V extends Value<E>> T impl$apply(final Key<V> key, final BiFunction<DataProvider, DataHolder, T> function, final Supplier<T> defaultResult) {
        for (final DataHolder dataHolder : this.impl$delegateDataHolder()) {
            final DataProvider<V, E> dataProvider = this.impl$getProviderFor(key, dataHolder);
            if (dataProvider.isSupported(dataHolder)) {
                return function.apply(dataProvider, dataHolder);
            }
        }
        return defaultResult.get();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    default boolean supports(final Key<?> key) {
        // XX: JDT cannot infer this cast
        return this.impl$apply((Key) key, (p, d) -> true, () -> false);
    }

    @Override
    default <E> Optional<E> get(final Key<? extends Value<E>> key) {
        return this.impl$apply(key, DataProvider::get, Optional::empty);
    }

    @Override
    default <E, V extends Value<E>> Optional<V> getValue(final Key<V> key) {
        return this.impl$apply(key, DataProvider::value, Optional::empty);
    }

    @SuppressWarnings("methodref.receiver")
    default Map<Key<?>, Object> impl$getMappedValues() {
        return this.impl$delegateDataHolder().stream()
                .flatMap(dh -> this.impl$getAllProviders(dh).stream()
                        .map(provider -> provider.value(dh).orElse(null))
                        .filter(Objects::nonNull)
                        .map(Value::asImmutable))
                .collect(ImmutableMap.toImmutableMap(Value::key, Value::get));
    }

    @Override
    default Set<Key<?>> getKeys() {
        return this.impl$delegateDataHolder().stream()
                .flatMap(dh -> this.impl$getAllProviders(dh).stream()
                        .filter(provider -> provider.get(dh).isPresent()).map(DataProvider::key))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    @SuppressWarnings("methodref.receiver")
    default Set<Value.Immutable<?>> getValues() {
        return this.impl$delegateDataHolder().stream()
                .flatMap(dh -> this.impl$getAllProviders(dh).stream()
                        .map(provider -> provider.value(dh).orElse(null))
                        .filter(Objects::nonNull)
                        .map(Value::asImmutable))
                .collect(ImmutableSet.toImmutableSet());
    }
}
