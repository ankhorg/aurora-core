package org.inksnow.core.nbt;

public interface FloatTag extends NumericTag {
    @Override
    default byte getId() {
        return Tag.TAG_FLOAT;
    }

    static FloatTag of(float value) {
        return TagFactory.instance().floatTag(value);
    }
}
