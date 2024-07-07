package org.inksnow.core.nbt;

public interface ShortTag extends NumericTag {
    @Override
    default byte getId() {
        return Tag.TAG_SHORT;
    }

    static ShortTag of(short value) {
        return TagFactory.instance().shortTag(value);
    }
}
