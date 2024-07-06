package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class DataUtil {
    public static <E> Collection<E> ensureMutable(Collection<E> collection) {
        if (collection instanceof List) {
            return DataUtil.ensureMutable((List<E>) collection);
        }
        if (collection instanceof Set) {
            return DataUtil.ensureMutable((Set<E>) collection);
        }
        return new ArrayList<>(collection);
    }

    public static <E> List<E> ensureMutable(List<E> list) {
        final Class<?> type = list.getClass();
        if (type == ArrayList.class
                || type == LinkedList.class
                || type == CopyOnWriteArrayList.class
                || type == Stack.class
                || type == Vector.class) {
            return list;
        }
        return new ArrayList<>(list);
    }

    public static <E> Set<E> ensureMutable(Set<E> set) {
        final Class<?> type = set.getClass();
        if (type == HashSet.class
                || type == LinkedHashSet.class
                || type == ConcurrentSkipListSet.class) {
            return set;
        }
        return new LinkedHashSet<>(set);
    }

    public static <K, V> Map<K, V> ensureMutable(Map<K, V> map) {
        final Class<?> type = map.getClass();
        if (type == HashMap.class
                || type == LinkedHashMap.class
                || type == TreeMap.class
                || type == ConcurrentHashMap.class) {
            return map;
        }
        return new LinkedHashMap<>(map);
    }
}
