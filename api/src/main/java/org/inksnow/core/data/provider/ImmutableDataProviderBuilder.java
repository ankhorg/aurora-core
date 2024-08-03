package org.inksnow.core.data.provider;

import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.util.Builder;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ImmutableDataProviderBuilder<H extends DataHolder, V extends Value<E>, E> extends
        Builder<DataProvider<? extends Value<E>, E>, ImmutableDataProviderBuilder<H, V, E>> {

    <NV extends Value<NE>, NE> ImmutableDataProviderBuilder<H, NV, NE> key(Key<NV> key);

    <NH extends H> ImmutableDataProviderBuilder<NH, V, E> dataHolder(TypeToken<NH> holder);

    <NH extends H> ImmutableDataProviderBuilder<NH, V, E> dataHolder(Class<NH> holder);

    ImmutableDataProviderBuilder<H, V, E> get(Function<H, E> get);

    ImmutableDataProviderBuilder<H, V, E> set(BiFunction<H, E, H> set);

    ImmutableDataProviderBuilder<H, V, E> supports(final Function<H, Boolean> supports);

    @Override
    DataProvider<? extends Value<E>, E> build();
}
