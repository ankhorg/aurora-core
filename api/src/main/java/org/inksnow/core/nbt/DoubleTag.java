package org.inksnow.core.nbt;

public interface DoubleTag extends NumericTag {
    @Override
    default byte getId() {
        return Tag.TAG_DOUBLE;
    }

    static DoubleTag of(double value) {
        return TagFactory.instance().doubleTag(value);
    }
}
