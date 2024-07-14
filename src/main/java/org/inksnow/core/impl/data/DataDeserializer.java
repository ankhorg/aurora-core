package org.inksnow.core.impl.data;

import io.leangen.geantyref.GenericTypeReflector;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataSerializable;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.impl.util.TypeTokenUtil;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DataDeserializer {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> BiFunction<DataView, DataQuery, Optional<T>> deserializer(final Type elementType) {
        final Class<?> rawType = GenericTypeReflector.erase(elementType);
        if (DataView.class.isAssignableFrom(rawType)) {
            return (view, dataQuery) -> (Optional<T>) view.getView(dataQuery);
        }
        if (DataSerializable.class.isAssignableFrom(rawType)) {
            return (view, dataQuery) -> (Optional<T>) view.getSerializable(dataQuery, (Class<? extends DataSerializable>) rawType);
        }
        if (AuroraDataManager.INSTANCE.translator(rawType).isPresent()) {
            return (view, dataQuery) -> (Optional<T>) view.getObject(dataQuery, rawType);
        }
        if (ResourcePath.class.isAssignableFrom(rawType)) {
            return (view, dataQuery) -> (Optional<T>) view.getString(dataQuery).map(ResourcePath::of);
        }
        if (Set.class.isAssignableFrom(rawType)) {
            final Type listType = ((ParameterizedType) elementType).getActualTypeArguments()[0];
            return (view, dataQuery) -> (Optional<T>) DataDeserializer.deserializeList((Class<?>) listType, view, dataQuery).map(list -> new HashSet(list));
        }
        if (List.class.isAssignableFrom(rawType)) {
            final Type listType = ((ParameterizedType) elementType).getActualTypeArguments()[0];
            return (view, dataQuery) -> (Optional<T>) DataDeserializer.deserializeList((Class<?>) listType, view, dataQuery);
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            throw new UnsupportedOperationException("Collection deserialization is not supported. Provide the deserializer for it.");
        }
        if (TypeTokenUtil.isArray(elementType)) {
            final Class arrayType = GenericTypeReflector.erase(GenericTypeReflector.getArrayComponentType(elementType));
            return (view, dataQuery) -> (Optional<T>) DataDeserializer.deserializeList((Class<?>) arrayType, view, dataQuery).map(list -> DataDeserializer.listToArray(arrayType, list));
        }
        if (Map.class.isAssignableFrom(rawType)) {
            final Type[] parameterTypes = ((ParameterizedType) elementType).getActualTypeArguments();
            final Type keyType = parameterTypes[0];
            final Type valueType = parameterTypes[1];
            if (!(keyType instanceof Class)) {
                throw new UnsupportedOperationException("Unsupported map-key type " + keyType);
            }
            final Function<DataQuery, ?> keyDeserializer = DataDeserializer.mapKeyDeserializer(keyType);
            final BiFunction<DataView, DataQuery, Optional<Object>> valueDeserializer = DataDeserializer.deserializer(valueType);
            return (view, dataQuery) -> (Optional<T>) DataDeserializer.deserializeMap(view, dataQuery, keyDeserializer, valueDeserializer);
        }
        return (view, dataQuery) -> (Optional<T>) view.get(dataQuery);
    }

    private static Function<DataQuery, ?> mapKeyDeserializer(Type keyType) {
        if (((Class<?>) keyType).isEnum()) {
            return key -> Enum.valueOf(((Class<? extends Enum>) keyType), key.toString());
        }
        if (keyType == String.class) {
            return DataQuery::toString;
        }
        if (keyType == UUID.class) {
            return key -> UUID.fromString(key.toString());
        }
        if (keyType == ResourcePath.class) {
            return key -> ResourcePath.of(key.toString());
        }
        if (keyType == Integer.class) {
            return key -> Integer.valueOf(key.toString());
        }
        // TODO other number types?
        throw new UnsupportedOperationException("Unsupported map-key type " + keyType);
    }

    private static Optional<?> deserializeMap(final DataView view, final DataQuery dataQuery,
            final Function<DataQuery, ?> keyDeserializer,
            final BiFunction<DataView, DataQuery, Optional<Object>> valueDeserializer) {
        return view.getView(dataQuery).map(mapView -> {
            final Map<Object, Object> resultMap = new HashMap<>();
            for (final DataQuery key : mapView.keys(false)) {
                final Object mapKey = keyDeserializer.apply(key);
                final Optional<?> mapValue = valueDeserializer.apply(mapView, key);
                resultMap.put(mapKey, mapValue.get());
            }
            return resultMap;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> Optional<List<T>> deserializeList(final Class<T> listType, final DataView view, final DataQuery dataQuery) {
        if (DataView.class.isAssignableFrom(listType)) {
            return (Optional) view.getViewList(dataQuery);
        }
        if (DataSerializable.class.isAssignableFrom(listType)) {
            return (Optional) view.getSerializableList(dataQuery, (Class<? extends DataSerializable>) listType);
        }
        if (AuroraDataManager.INSTANCE.translator(listType).isPresent()) {
            return view.getObjectList(dataQuery, listType);
        }
        return (Optional) view.getList(dataQuery);
    }

    private static <AT> AT[] listToArray(final Class<AT> componentType, final List<AT> list) {
        return list.toArray((AT[]) Array.newInstance(componentType, list.size()));
    }
}
