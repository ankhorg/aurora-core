package org.inksnow.core.impl.nbt;

import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.nbt.Tag;

public interface AuroraTag<R extends RefNbtBase> extends Tag {
    R impl$ref();

    @Override
    default String getAsString() {
        return impl$ref().asString();
    }
}
