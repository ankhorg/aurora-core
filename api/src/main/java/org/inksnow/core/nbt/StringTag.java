package org.inksnow.core.nbt;

public interface StringTag extends Tag {
    @Override
    default byte getId() {
        return Tag.TAG_STRING;
    }

    static StringTag of(String value) {
        return TagFactory.instance().stringTag(value);
    }
}
