package org.inksnow.core.impl;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;

public class Keys {
    public static final Key<Value<String>> NAME = Key.builder()
            .resourcePath(ResourcePath.of("minecraft:name"))
            .elementType(String.class)
            .build();

    public static final Key<Value<String>> CUSTOM_NAME = Key.builder()
            .resourcePath(ResourcePath.of("minecraft:custom_name"))
            .elementType(String.class)
            .build();

    public static final Key<Value<Double>> HEALTH = Key.builder()
            .resourcePath(ResourcePath.of("minecraft:health"))
            .elementType(Double.class)
            .build();
}
