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

    EmptyDataProvider<V, E> emptyDataProvider;
    ValueConstructor<V, E> valueConstructor;

    public AuroraKey(ResourcePath resourcePath, Type valueType, Type elementType, Supplier<@Nullable E> defaultValueSupplier) {
        this.resourcePath = resourcePath;
        this.valueType = valueType;
        this.elementType = elementType;
        this.defaultValueSupplier = defaultValueSupplier;

        this.emptyDataProvider = new EmptyDataProvider<>(this);
        this.valueConstructor = ValueConstructorFactory.getConstructor(this);
    }
}
