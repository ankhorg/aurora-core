package org.inksnow.core.impl.data.key;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.provider.EmptyDataProvider;
import org.inksnow.core.impl.data.value.ValueConstructor;
import org.inksnow.core.impl.data.value.ValueConstructorFactory;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Type;
import java.util.function.Supplier;

@Getter
@lombok.Value
public class AuroraKey<V extends Value<E>, E> implements Key<V> {
    ResourcePath resourcePath;
    Type valueType;
    Type elementType;
    Supplier<@Nullable E> defaultValueSupplier;

    @Getter(lazy = true)
    EmptyDataProvider<V, E> emptyDataProvider = new EmptyDataProvider<>(this);
    @Getter(lazy = true)
    ValueConstructor<V, E> valueConstructor = ValueConstructorFactory.getConstructor(this);
}
