package org.inksnow.core.impl;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;

public class Keys {
    public static final Key<Value<Double>> HEALTH = Key.builder()
            .resourcePath(ResourcePath.of("aurora:entity:health"))
            .elementType(Double.class)
            .build();

    public static final Key<Value<Integer>> PLAYER_MESSAGE_COUNTER = Key.builder()
            .resourcePath(ResourcePath.of("aurora:player:message_counter"))
            .elementType(Integer.class)
            .build();

    public static final Key<Value<Integer>> BLOCK_CLICK_COUNTER = Key.builder()
            .resourcePath(ResourcePath.of("aurora:block:click_counter"))
            .elementType(Integer.class)
            .build();
}
