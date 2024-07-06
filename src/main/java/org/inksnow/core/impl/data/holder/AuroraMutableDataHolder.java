package org.inksnow.core.impl.data.holder;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.CollectionValue;
import org.inksnow.core.data.value.MapValue;
import org.inksnow.core.data.value.MergeFunction;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.data.value.ValueContainer;
import org.inksnow.core.impl.data.key.AuroraKey;
import org.inksnow.core.impl.util.DataUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface AuroraMutableDataHolder extends AuroraDataHolder, DataHolder.Mutable {
    // Implementation Utility

    default List<Mutable> impl$mutableDelegateDataHolder() {
        return this.impl$delegateDataHolder().stream()
                .filter(Mutable.class::isInstance)
                .map(DataHolder.Mutable.class::cast)
                .collect(Collectors.toList());
    }

    default <E, V extends Value<E>> DataTransactionResult impl$applyTransaction(Key<V> key, BiFunction<DataProvider<V, E>, Mutable, DataTransactionResult> function, Supplier<DataTransactionResult> defaultResult) {
        for (Mutable dataHolder : this.impl$mutableDelegateDataHolder()) {
            // Offer to the first available mutable data holder
            final DataProvider<V, E> dataProvider = this.impl$getProviderFor(key, dataHolder);
            if (dataProvider.isSupported(dataHolder)) {
                return function.apply(dataProvider, dataHolder);
            }
        }
        return defaultResult.get();
    }

    // Mutable Implementation
    @Override
    default <E> DataTransactionResult offer(Key<? extends Value<E>> key, E value) {
        return this.impl$applyTransaction(key, (p, m) -> p.offer(m, value),
                () -> DataTransactionResult.failResult(Value.immutableOf(key, value)));
    }

    @Override
    default DataTransactionResult offer(Value<?> value) {
        return this.impl$applyTransaction(value.key(), (p, m) -> ((DataProvider<Value<?>, ?>) p).offerValue(m, value),
                () -> DataTransactionResult.failResult(value.asImmutable()));
    }

    @Override
    default <E> DataTransactionResult offerSingle(Key<? extends CollectionValue<E, ?>> key, E element) {
        final AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>> key0 =
                (AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>>) key;
        return this.impl$applyTransaction(key0, (p, m) -> {
                    final @Nullable Collection<E> collection = p.get(m)
                            .map(DataUtil::ensureMutable)
                            .orElseGet(key0.defaultValueSupplier());
                    Preconditions.checkState(collection != null, "Collection is null, it should never happened");
                    if (!collection.add(element)) {
                        return DataTransactionResult.failNoData();
                    }
                    return p.offer(m, collection);
                },
                DataTransactionResult::failNoData);
    }

    @Override
    default <E> DataTransactionResult offerAll(Key<? extends CollectionValue<E, ?>> key, Collection<? extends E> elements) {
        final AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>> key0 =
                (AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>>) key;
        return this.impl$applyTransaction(key0, (p, m) -> {
                    final @Nullable Collection<E> collection = p.get(m)
                            .map(DataUtil::ensureMutable)
                            .orElseGet(key0.defaultValueSupplier());
                    Preconditions.checkState(collection != null, "Collection is null, it should never happened");
                    if (!collection.addAll(elements)) {
                        return DataTransactionResult.failNoData();
                    }
                    return p.offer(m, collection);
                },
                DataTransactionResult::failNoData);
    }

    @Override
    default <K, V> DataTransactionResult offerSingle(Key<? extends MapValue<K, V>> key, K valueKey, V value) {
        return this.impl$applyTransaction(key, (p, m) -> {
                    final Map<K, V> kvMap = p.get(m).map(DataUtil::ensureMutable).orElseGet(((AuroraKey) key).defaultValueSupplier());
                    kvMap.put(valueKey, value);
                    return p.offer(m, kvMap);
                },
                DataTransactionResult::failNoData);
    }

    @Override
    default <K, V> DataTransactionResult offerAll(Key<? extends MapValue<K, V>> key, Map<? extends K, ? extends V> values) {
        if (values.isEmpty()) {
            return DataTransactionResult.failNoData();
        }
        return this.impl$applyTransaction(key, (p, m) -> {
                    final Map<K, V> kvMap = p.get(m).map(DataUtil::ensureMutable).orElseGet(((AuroraKey) key).defaultValueSupplier());
                    kvMap.putAll(values);
                    return p.offer(m, kvMap);
                },
                DataTransactionResult::failNoData);
    }

    @Override
    default <E> DataTransactionResult removeSingle(Key<? extends CollectionValue<E, ?>> key, E element) {
        final AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>> key0 =
                (AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>>) key;
        return this.impl$applyTransaction(key0, (p, m) -> {
            final Optional<Collection<E>> optCollection = p.get(m).map(DataUtil::ensureMutable);
            if (!optCollection.isPresent()) {
                return DataTransactionResult.failNoData();
            }
            final Collection<E> collection = optCollection.get();
            if (!collection.remove(element)) {
                return DataTransactionResult.failNoData();
            }
            return p.offer(m, collection);
        }, DataTransactionResult::failNoData);
    }

    @Override
    default <E> DataTransactionResult removeAll(Key<? extends CollectionValue<E, ?>> key, Collection<? extends E> elements) {
        if (elements.isEmpty()) {
            return DataTransactionResult.failNoData();
        }
        final AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>> key0 =
                (AuroraKey<? extends CollectionValue<E, Collection<E>>, Collection<E>>) key;
        return this.impl$applyTransaction(key0, (p, m) -> {
            final Optional<Collection<E>> optCollection = p.get(m).map(DataUtil::ensureMutable);
            if (!optCollection.isPresent()) {
                return DataTransactionResult.failNoData();
            }
            final Collection<E> collection = optCollection.get();
            if (!collection.removeAll(elements)) {
                return DataTransactionResult.failNoData();
            }
            return p.offer(m, collection);
        }, DataTransactionResult::failNoData);
    }


    @Override
    default <K> DataTransactionResult removeKey(Key<? extends MapValue<K, ?>> key, K mapKey) {
        final AuroraKey<? extends MapValue<K, Object>, Map<K, Object>> key0 =
                (AuroraKey<? extends MapValue<K, Object>, Map<K, Object>>) key;
        return this.impl$applyTransaction(key0, (p, m) -> {
            final Optional<? extends Map<K, ?>> optMap = p.get(m).map(DataUtil::ensureMutable);
            if (!optMap.isPresent() || !optMap.get().containsKey(mapKey)) {
                return DataTransactionResult.failNoData();
            }
            final Map<K, ?> map = optMap.get();
            map.remove(mapKey);
            return ((DataProvider) p).offer(m, map);
        }, DataTransactionResult::failNoData);
    }

    @Override
    default <K, V> DataTransactionResult removeAll(Key<? extends MapValue<K, V>> key, Map<? extends K, ? extends V> values) {
        if (values.isEmpty()) {
            return DataTransactionResult.failNoData();
        }
        return this.impl$applyTransaction(key, (p, m) -> {
            final Optional<? extends Map<K, ?>> optMap = p.get(m).map(DataUtil::ensureMutable);
            if (!optMap.isPresent()) {
                return DataTransactionResult.failNoData();
            }
            final Map<K, ?> map = optMap.get();
            for (final Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
                map.remove(entry.getKey(), entry.getValue());
            }
            return ((DataProvider) p).offer(m, map);
        }, DataTransactionResult::failNoData);
    }

    @Override
    default DataTransactionResult remove(Key<?> key) {
        return this.impl$applyTransaction((Key) key, DataProvider::remove, DataTransactionResult::failNoData);
    }

    @Override
    default DataTransactionResult remove(Value<?> value) {
        return this.impl$applyTransaction(value.key(), (p, m) -> {
            final Optional<?> opt = p.get(m);
            if (opt.isPresent() && opt.get().equals(value.get())) {
                return p.remove(m);
            }
            return DataTransactionResult.failNoData();
        }, DataTransactionResult::failNoData);
    }

    @Override
    default DataTransactionResult copyFrom(ValueContainer that, MergeFunction function) {
        Objects.requireNonNull(that, "that");
        Objects.requireNonNull(function, "function");
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        boolean success = false;
        if (function == MergeFunction.REPLACEMENT_PREFERRED) {
            // Produce less garbage if we know we don't have to merge any values
            for (final Value<?> replacement : that.getValues()) {
                final DataTransactionResult result = this.offer(replacement);
                builder.absorbResult(result);
                if (result.isSuccessful()) {
                    success = true;
                }
            }
        } else if (function == MergeFunction.ORIGINAL_PREFERRED) {
            // Produce less garbage if we know we don't have to merge any values
            for (final Value replacement : that.getValues()) {
                final Key<Value<Object>> key = replacement.key();
                if (this.get(key).isPresent()) {
                    continue;
                }
                final Value merged = function.merge(null, replacement);
                final DataTransactionResult result = this.offer(merged);
                builder.absorbResult(result);
                if (result.isSuccessful()) {
                    success = true;
                }
            }
        } else {
            for (final Value replacement : that.getValues()) {
                final Key<Value<Object>> key = replacement.key();
                final @Nullable Value original = this.getValue(key).map(Value::asImmutable).orElse(null);
                final Value merged = function.merge(original, replacement);
                final DataTransactionResult result = this.offer(merged);
                builder.absorbResult(result);
                if (result.isSuccessful()) {
                    success = true;
                }
            }
        }
        if (success) {
            builder.result(DataTransactionResult.Type.SUCCESS);
        } else {
            builder.result(DataTransactionResult.Type.FAILURE);
        }
        return builder.build();
    }

    @Override
    default DataTransactionResult undo(DataTransactionResult result) {
        if (result.replacedData().isEmpty() && result.successfulData().isEmpty()) {
            return DataTransactionResult.successNoData();
        }
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        for (final Value<?> value : result.replacedData()) {
            builder.absorbResult(this.offer(value));
        }
        for (final Value<?> value : result.successfulData()) {
            builder.absorbResult(this.remove(value));
        }
        return DataTransactionResult.failNoData();
    }

    // Delegated

    @Override
    default <E> DataTransactionResult tryOffer(Key<? extends Value<E>> key, E value) {
        final DataTransactionResult result = this.offer(key, value);
        if (!result.isSuccessful()) {
            throw new IllegalArgumentException("Failed offer transaction!");
        }
        return result;
    }

    @Override
    default DataTransactionResult offerAll(CollectionValue<?, ?> value) {
        return this.offerAll((Key<? extends CollectionValue<Object, ?>>) value.key(), value.get());
    }

    @Override
    default DataTransactionResult offerAll(MapValue<?, ?> value) {
        return this.offerAll((Key<? extends MapValue<Object, Object>>) value.key(), value.get());
    }

    @Override
    default DataTransactionResult removeAll(CollectionValue<?, ?> value) {
        return this.removeAll((Key<? extends CollectionValue<Object, ?>>) value.key(), value.get());
    }

    @Override
    default DataTransactionResult removeAll(MapValue<?, ?> value) {
        return this.removeAll((Key<? extends MapValue<Object, Object>>) value.key(), value.get());
    }
}
