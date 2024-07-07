package org.inksnow.core.nbt;

public interface LongTag extends NumericTag {
    @Override
    default byte getId() {
        return Tag.TAG_LONG;
    }

    static LongTag of(long value) {
        return TagFactory.instance().longTag(value);
    }
}
