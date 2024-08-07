package org.inksnow.core.impl.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.core.util.Copyable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes", "type.argument", "type.arguments.not.inferred"})
public final class CopyHelper {

    @SuppressWarnings("return")
    public static <T> T copy(T value) {
        if (value instanceof Copyable) {
            return (T) ((Copyable) value).copy();
        }
        if (value instanceof Map) {
            return (T) CopyHelper.copyMap((Map<?, ?>) value);
        }
        if (value instanceof List) {
            return (T) CopyHelper.copyList((List<?>) value);
        }
        return value;
    }

    public static <L extends List<E>, E> L copyList(L list) {
        final boolean copyElements;
        if (list.isEmpty()) {
            copyElements = false;
        } else {
            final E first = list.get(0);
            copyElements = CopyHelper.copy(first) == first;
        }
        if (list instanceof ImmutableList) {
            if (copyElements) {
                return (L) list.stream().map(CopyHelper::copy).collect(ImmutableList.toImmutableList());
            }
            return list;
        }
        final L copy;
        final Class<?> type = list.getClass();
        if (type == LinkedList.class) {
            if (copyElements) {
                copy = (L) new LinkedList<E>();
                list.forEach(element -> copy.add(CopyHelper.copy(element)));
            } else {
                copy = (L) new LinkedList<>(list);
            }
        } else if (type == CopyOnWriteArrayList.class) {
            if (copyElements) {
                copy = (L) new CopyOnWriteArrayList<>(list.stream()
                        .map(CopyHelper::copy)
                        .collect(Collectors.toList()));
            } else {
                copy = (L) new CopyOnWriteArrayList(list);
            }
        } else {
            if (copyElements) {
                copy = (L) new ArrayList<E>(list.size());
                list.forEach(element -> copy.add(CopyHelper.copy(element)));
            } else {
                copy = (L) new ArrayList<>(list);
            }
        }
        return copy;
    }

    public static <M extends Map<K, V>, K, V> @NonNull M copyMap(@NonNull M map) {
        if (map instanceof Copyable) {
            return (M) ((Copyable) map).copy();
        }
        final boolean copyEntries;
        if (map.isEmpty()) {
            copyEntries = false;
        } else {
            final Map.Entry<K, V> firstEntry = map.entrySet().iterator().next();
            copyEntries = CopyHelper.copy(firstEntry.getKey()) == firstEntry.getKey()
                    || CopyHelper.copy(firstEntry.getValue()) == firstEntry.getValue();
        }
        if (map instanceof ImmutableMap) {
            if (copyEntries) {
                final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
                map.forEach((key, value) -> builder.put(CopyHelper.copy(key), CopyHelper.copy(value)));
                return (M) builder.build();
            }
            return map;
        }
        final M copy;
        final Class<?> type = map.getClass();
        if (type == HashMap.class) {
            if (copyEntries) {
                copy = (M) new HashMap<K, V>();
                map.forEach((key, value) -> copy.put(CopyHelper.copy(key), CopyHelper.copy(value)));
            } else {
                copy = (M) new HashMap<>(map);
            }
        } else {
            if (copyEntries) {
                copy = (M) new LinkedHashMap<K, V>();
                map.forEach((key, value) -> copy.put(CopyHelper.copy(key), CopyHelper.copy(value)));
            } else {
                copy = (M) new LinkedHashMap<>(map);
            }
        }
        return copy;
    }

    /**
     * Creates a {@link Supplier} which creates copies
     * of the provided value, if needed.
     *
     * @param value The value to create the supplier from
     * @param <T>   The value type
     * @return The constructed supplier
     */
    public static <T> Supplier<T> createSupplier(T value) {
        final T copy = CopyHelper.copy(value);
        if (copy == value) {
            return () -> value;
        } else {
            return () -> CopyHelper.copy(copy);
        }
    }
}
