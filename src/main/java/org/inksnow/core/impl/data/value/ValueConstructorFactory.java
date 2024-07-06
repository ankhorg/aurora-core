package org.inksnow.core.impl.data.value;

import io.leangen.geantyref.GenericTypeReflector;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKey;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class ValueConstructorFactory {

    public static <V extends Value<E>, E> ValueConstructor<V, E> getConstructor(final AuroraKey<V, E> key) {
        final Class<?> valueType = GenericTypeReflector.erase(key.valueType());
        ValueConstructor<V, E> valueConstructor;
        /* if (ListValue.class.isAssignableFrom(valueType)) {
            valueConstructor = new SimpleValueConstructor(key,
                    (key1, value) -> new MutableSpongeListValue((Key<? extends ListValue>) key1, (List) value),
                    (key1, value) -> new ImmutableSpongeListValue((Key<? extends ListValue>) key1, (List) value));
        } else if (SetValue.class.isAssignableFrom(valueType)) {
            valueConstructor = new SimpleValueConstructor(key,
                    (key1, value) -> new MutableSpongeSetValue((Key<? extends SetValue>) key1, (Set) value),
                    (key1, value) -> new ImmutableSpongeSetValue((Key<? extends SetValue>) key1, (Set) value));
        } else if (MapValue.class.isAssignableFrom(valueType)) {
            valueConstructor = new SimpleValueConstructor(key,
                    (key1, value) -> new MutableSpongeMapValue((Key<? extends MapValue>) key1, (Map) value),
                    (key1, value) -> new ImmutableSpongeMapValue((Key<? extends MapValue>) key1, (Map) value));
        } else */
        {
            valueConstructor = new SimpleValueConstructor(key,
                    (key1, value) -> new MutableAuroraValue((Key<? extends Value>) key1, value),
                    (key1, value) -> new ImmutableAuroraValue((Key<? extends Value>) key1, value));
            final Class<?> elementType = GenericTypeReflector.erase(key.elementType());
            if (Enum.class.isAssignableFrom(elementType)) {
                valueConstructor = new CachedEnumValueConstructor(valueConstructor, elementType);
            } else if (elementType == Boolean.class) {
                valueConstructor = (ValueConstructor<V, E>) new CachedBooleanValueConstructor(
                        (ValueConstructor<Value<Boolean>, Boolean>) valueConstructor);
            }
        }
        return valueConstructor;
    }
}
