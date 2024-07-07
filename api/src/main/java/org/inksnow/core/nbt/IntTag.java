package org.inksnow.core.nbt;

public interface IntTag extends NumericTag {
    @Override
    default byte getId() {
        return Tag.TAG_INT;
    }

    static IntTag of(int value) {
        return TagFactory.instance().intTag(value);
    }
}
