package org.inksnow.core.impl.data.key;

import com.google.common.base.Preconditions;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.ListValue;
import org.inksnow.core.data.value.MapValue;
import org.inksnow.core.data.value.SetValue;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class AuroraKeyBuilder<E, V extends Value<E>> implements Key.Builder<E, V> {
    private @MonotonicNonNull ResourcePath resourcePath;
    private @MonotonicNonNull Type valueType;
    private @MonotonicNonNull Type elementType;

    @Override
    public @This AuroraKeyBuilder<E, V> resourcePath(final ResourcePath resourcePath) {
        this.resourcePath = resourcePath;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, B extends Value<T>> @This AuroraKeyBuilder<T, B> type(final TypeToken<B> token) {
        Preconditions.checkNotNull(token, "token");
        this.valueType = token.getType();
        final Type valueTypeAsSuper = GenericTypeReflector.getExactSuperType(this.valueType, Value.class);
        if (!(valueTypeAsSuper instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Raw type " + this.valueType + " provided when registering Key " + this.resourcePath);
        }
        this.elementType = ((ParameterizedType) valueTypeAsSuper).getActualTypeArguments()[0];
        return (AuroraKeyBuilder<T, B>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @This AuroraKeyBuilder<T, Value<T>> elementType(final Class<T> type) {
        Preconditions.checkNotNull(type, "type");
        this.valueType = TypeFactory.parameterizedClass(Value.class, type);
        this.elementType = type;
        return (AuroraKeyBuilder<T, Value<T>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @This AuroraKeyBuilder<T, Value<T>> elementType(final TypeToken<T> type) {
        Preconditions.checkNotNull(type, "type");
        this.valueType = TypeFactory.parameterizedClass(Value.class, type.getType());
        this.elementType = type.getType();
        return (AuroraKeyBuilder<T, Value<T>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @This AuroraKeyBuilder<List<T>, ListValue<T>> listElementType(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        this.valueType = TypeFactory.parameterizedClass(ListValue.class, type);
        this.elementType = TypeFactory.parameterizedClass(List.class, type);
        return (AuroraKeyBuilder<List<T>, ListValue<T>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @This AuroraKeyBuilder<List<T>, ListValue<T>> listElementType(final TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        this.valueType = TypeFactory.parameterizedClass(ListValue.class, type.getType());
        this.elementType = TypeFactory.parameterizedClass(List.class, type.getType());
        return (AuroraKeyBuilder<List<T>, ListValue<T>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @This AuroraKeyBuilder<Set<T>, SetValue<T>> setElementType(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        this.valueType = TypeFactory.parameterizedClass(SetValue.class, type);
        this.elementType = TypeFactory.parameterizedClass(Set.class, type);
        return (AuroraKeyBuilder<Set<T>, SetValue<T>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @This AuroraKeyBuilder<Set<T>, SetValue<T>> setElementType(final TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        this.valueType = TypeFactory.parameterizedClass(SetValue.class, type.getType());
        this.elementType = TypeFactory.parameterizedClass(Set.class, type.getType());
        return (AuroraKeyBuilder<Set<T>, SetValue<T>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V1> @This AuroraKeyBuilder<Map<K, V1>, MapValue<K, V1>> mapElementType(final Class<K> keyType, final Class<V1> valueType) {
        Objects.requireNonNull(keyType, "keyType");
        Objects.requireNonNull(valueType, "valueType");
        this.valueType = TypeFactory.parameterizedClass(MapValue.class, keyType, valueType);
        this.elementType = TypeFactory.parameterizedClass(Map.class, keyType, valueType);
        return (AuroraKeyBuilder<Map<K, V1>, MapValue<K, V1>>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V1> @This AuroraKeyBuilder<Map<K, V1>, MapValue<K, V1>> mapElementType(final TypeToken<K> keyType, final TypeToken<V1> valueType) {
        Objects.requireNonNull(keyType, "keyType");
        Objects.requireNonNull(valueType, "valueType");
        this.valueType = TypeFactory.parameterizedClass(MapValue.class, keyType.getType(), valueType.getType());
        this.elementType = TypeFactory.parameterizedClass(Map.class, keyType.getType(), valueType.getType());
        return (AuroraKeyBuilder<Map<K, V1>, MapValue<K, V1>>) this;
    }

    @Override
    @SuppressWarnings("assignment") // reset method
    public @This AuroraKeyBuilder<E, V> reset() {
        this.valueType = null;
        this.elementType = null;
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AuroraKey<V, E> build() {
        final @Nullable ResourcePath resourcePath = this.resourcePath;
        final @Nullable Type valueType = this.valueType;
        final @Nullable Type elementType = this.elementType;

        Preconditions.checkState(resourcePath != null, "Resource path is not set");
        Preconditions.checkState(valueType != null, "Value type is not set");
        Preconditions.checkState(elementType != null, "Element type is not set");

        Supplier<@Nullable E> defaultValueSupplier = () -> null;
        final Class<?> rawType = GenericTypeReflector.erase(valueType);
        if (ListValue.class.isAssignableFrom(rawType)) {
            defaultValueSupplier = () -> (E) new ArrayList();
        } else if (SetValue.class.isAssignableFrom(rawType)) {
            defaultValueSupplier = () -> (E) new HashSet();
        } else if (MapValue.class.isAssignableFrom(rawType)) {
            defaultValueSupplier = () -> (E) new HashMap<>();
        }

        return new AuroraKey<V, E>(resourcePath, valueType, elementType, defaultValueSupplier);
    }
}
