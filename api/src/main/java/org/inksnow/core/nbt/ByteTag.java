package org.inksnow.core.nbt;

public interface ByteTag extends NumericTag {

    @Override
    default byte getId() {
        return Tag.TAG_BYTE;
    }

    @Override
    ByteTag copy();

    static ByteTag of(byte value) {
        return TagFactory.instance().byteTag(value);
    }

    static ByteTag of(boolean value) {
        return TagFactory.instance().byteTag(value);
    }
}
