package org.inksnow.core.nbt;

public interface LongArrayTag extends CollectionTag<LongTag> {
    @Override
    default byte getId() {
        return Tag.TAG_LONG_ARRAY;
    }

    @Override
    default byte getElementType() {
        return Tag.TAG_LONG;
    }

    long[] getLongArray();

    static LongArrayTag create(long[] data) {
        return TagFactory.instance().longArrayTag(data);
    }
}
