package org.inksnow.core.impl.data.provider.nbt;

import java.util.Objects;
import java.util.StringJoiner;

public final class NBTDataType {

    private final String name;

    public NBTDataType(final String name) {
        Objects.requireNonNull(name, "name");
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NBTDataType.class.getSimpleName() + "[", "]")
                .add("name=" + this.name)
                .toString();
    }
}
