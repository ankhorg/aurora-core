package org.inksnow.core.nbt;

import java.util.List;

public interface ByteArrayTag extends CollectionTag<ByteTag> {
    @Override
    default byte getId() {
        return Tag.TAG_BYTE_ARRAY;
    }

    @Override
    default byte getElementType() {
        return Tag.TAG_BYTE;
    }

    byte[] getByteArray();

    void setByteArray(byte[] data);

    static ByteArrayTag create(byte[] data) {
        return TagFactory.instance().byteArrayTag(data);
    }

    static ByteArrayTag create(List<Byte> data) {
        return TagFactory.instance().byteArrayTag(data);
    }
}
