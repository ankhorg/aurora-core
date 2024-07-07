package org.inksnow.core.nbt;

public interface ListTag extends CollectionTag<Tag> {
    @Override
    default byte getId() {
        return Tag.TAG_LIST;
    }

    CompoundTag getCompound(int index);

    ListTag getList(int index);

    short getShort(int index);

    int getInt(int index);

    int[] getIntArray(int index);

    long[] getLongArray(int index);

    double getDouble(int index);

    float getFloat(int index);

    String getString(int index);

    static ListTag create() {
        return TagFactory.instance().listTag();
    }
}
