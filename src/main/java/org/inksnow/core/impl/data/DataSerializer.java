package org.inksnow.core.impl.data;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataSerializable;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.resource.ResourcePath;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataSerializer {

    public static Object serialize(final DataView.SafetyMode safetyMode, final Object value) {
        if (value instanceof DataView) {
            switch (safetyMode) {
                case ALL_DATA_CLONED:
                case CLONED_ON_SET:
                    final MemoryDataView view = new MemoryDataContainer(safetyMode);
                    for (final Map.Entry<DataQuery, Object> entry : ((DataView) value).values(false).entrySet()) {
                        view.set(entry.getKey(), entry.getValue());
                    }
                    return view;
                default:
                    return value;
            }
        }

        if (value instanceof DataSerializable) {
            return ((DataSerializable) value).toContainer();
        }
        final Optional<? extends DataTranslator<?>> translator = AuroraDataManager.INSTANCE.translator(value.getClass());
        if (translator.isPresent()) {
            final DataTranslator serializer = translator.get();
            return serializer.translate(value);
        }

        if (value instanceof ResourcePath) {
            return ((ResourcePath) value).asString();
        }
        if (value instanceof Collection) {
            return DataSerializer.serializeCollection(safetyMode, (Collection<?>) value);
        }
        if (value instanceof Map) {
            return DataSerializer.serializeMap((Map<?, ?>) value);
        }
        if (value.getClass().isArray()) {
            return DataSerializer.serializeArray(safetyMode, value);
        }
        return value;
    }

    private static DataContainer serializeMap(final Map<?, ?> value) {
        final DataContainer map = DataContainer.createNew();
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            final DataQuery mapKey = DataSerializer.serializeMapKey(entry.getKey());
            map.set(mapKey, entry.getValue());
        }
        return map;
    }

    private static ImmutableList<Object> serializeCollection(final DataView.SafetyMode safetyMode, final Collection<?> value) {
        final ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (final Object object : value) {
            builder.add(DataSerializer.serialize(safetyMode, object));
        }
        return builder.build();
    }

    private static DataQuery serializeMapKey(final Object value) {
        if (value instanceof DataQuery) {
            return ((DataQuery) value);
        }
        if (value.getClass().isEnum()) {
            return DataQuery.of(((Enum<?>) value).name());
        }
        if (value instanceof UUID) {
            return DataQuery.of(value.toString());
        }
        if (value instanceof ResourcePath) {
            return DataQuery.of(value.toString());
        }
        if (value instanceof String) {
            return DataQuery.of(value.toString());
        }
        if (value instanceof Integer) {
            return DataQuery.of(value.toString());
        }
        throw new UnsupportedOperationException("Unsupported map-key type " + value.getClass());
    }

    @NonNull
    private static Object serializeArray(final DataView.SafetyMode safetyMode, final Object value) {
        switch (safetyMode) {
            case ALL_DATA_CLONED:
            case CLONED_ON_SET:
                break;
            default:
                return value;
        }
        if (value instanceof byte[]) {
            return ArrayUtils.clone((byte[]) value);
        }
        if (value instanceof short[]) {
            return ArrayUtils.clone((short[]) value);
        }
        if (value instanceof int[]) {
            return ArrayUtils.clone((int[]) value);
        }
        if (value instanceof long[]) {
            return ArrayUtils.clone((long[]) value);
        }
        if (value instanceof float[]) {
            return ArrayUtils.clone((float[]) value);
        }
        if (value instanceof double[]) {
            return ArrayUtils.clone((double[]) value);
        }
        if (value instanceof boolean[]) {
            return ArrayUtils.clone((boolean[]) value);
        }
        return ArrayUtils.clone((Object[]) value);
    }
}
