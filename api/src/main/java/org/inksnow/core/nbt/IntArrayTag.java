package org.inksnow.core.nbt;

public interface IntArrayTag extends CollectionTag<IntTag> {
    @Override
    default byte getId() {
        return Tag.TAG_INT_ARRAY;
    }

    @Override
    default byte getElementType() {
        return Tag.TAG_INT;
    }

    int[] getIntArray();

    static IntArrayTag create(int[] data) {
        return TagFactory.instance().intArrayTag(data);
    }
}
