package org.inksnow.core.nbt;

import java.util.List;

public interface CollectionTag<E extends Tag> extends List<E>, Tag {
    E set(int index, E tag);

    void add(int index, E tag);

    E remove(int index);

    boolean setTag(int index, Tag tag);

    boolean addTag(int index, Tag tag);

    byte getElementType();
}
