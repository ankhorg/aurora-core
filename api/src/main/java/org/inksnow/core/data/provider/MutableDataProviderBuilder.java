package org.inksnow.core.data.provider;

import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.util.Builder;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MutableDataProviderBuilder<H extends DataHolder.Mutable, V extends Value<E>, E> extends
        Builder<DataProvider<V, E>, MutableDataProviderBuilder<H, V, E>> {

    <NV extends Value<NE>, NE> MutableDataProviderBuilder<H, NV, NE> key(Key<NV> key);

    <NH extends H> MutableDataProviderBuilder<NH, V, E> dataHolder(TypeToken<NH> holder);

    <NH extends H> MutableDataProviderBuilder<NH, V, E> dataHolder(Class<NH> holder);

    MutableDataProviderBuilder<H, V, E> get(Function<H, E> get);

    MutableDataProviderBuilder<H, V, E> set(BiConsumer<H, E> set);

    MutableDataProviderBuilder<H, V, E> setAnd(BiFunction<H, E, Boolean> setAnd);

    MutableDataProviderBuilder<H, V, E> delete(Consumer<H> delete);

    MutableDataProviderBuilder<H, V, E> deleteAnd(Function<H, Boolean> delete);

    MutableDataProviderBuilder<H, V, E> deleteAndGet(Function<H, DataTransactionResult> delete);

    MutableDataProviderBuilder<H, V, E> resetOnDelete(Supplier<E> resetOnDeleteTo);

    MutableDataProviderBuilder<H, V, E> resetOnDelete(Function<H, E> resetOnDeleteTo);

    MutableDataProviderBuilder<H, V, E> setAndGet(BiFunction<H, E, DataTransactionResult> setAndGet);

    MutableDataProviderBuilder<H, V, E> supports(final Function<H, Boolean> supports);

    @Override
    DataProvider<V, E> build();
}
