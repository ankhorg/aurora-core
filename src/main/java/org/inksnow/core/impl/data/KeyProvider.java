package org.inksnow.core.impl.data;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KeyProvider {

    public static final KeyProvider INSTANCE = new KeyProvider();

    private final Map<ResourcePath, Key<Value<?>>> mappings;

    KeyProvider() {
        this.mappings = new HashMap<>();
    }

    public void register(final ResourcePath resourceKey, final Key<Value<?>> key) {
        this.mappings.put(resourceKey, key);
    }

    public <T extends Value<?>> Optional<Key<@NonNull T>> get(final ResourcePath resourceKey) {
        return (Optional<Key<T>>) (Object) Optional.ofNullable(this.mappings.get(resourceKey));
    }
}
