package org.inksnow.core.impl.nbt;

import org.apache.commons.lang.ArrayUtils;
import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagByte;
import org.inksnow.core.impl.ref.nbt.RefNbtTagByteArray;
import org.inksnow.core.nbt.ByteArrayTag;
import org.inksnow.core.nbt.ByteTag;
import org.inksnow.core.nbt.NumericTag;
import org.inksnow.core.nbt.Tag;

import java.util.Arrays;
import java.util.List;

public final class AuroraByteArrayTag extends AuroraCollectionTag<RefNbtTagByte, ByteTag> implements ByteArrayTag {
    private final RefNbtTagByteArray ref;

    /* package-private */ AuroraByteArrayTag(RefNbtTagByteArray ref) {
        this.ref = ref;
    }

    public AuroraByteArrayTag(byte[] data) {
        this(new RefNbtTagByteArray(data));
    }

    public AuroraByteArrayTag(List<Byte> data) {
        this(new RefNbtTagByteArray(data));
    }

    @Override
    public RefNbtBase impl$ref() {
        return ref;
    }

    @Override
    public byte[] getByteArray() {
        return ref.getBytes();
    }

    @Override
    public void setByteArray(byte[] data) {
        ref.data = data;
    }

    @Override
    public int size() {
        return ref.getBytes().length;
    }

    @Override
    public ByteTag get(int index) {
        return AuroraByteTag.of(ref.getBytes()[index]);
    }

    @Override
    public ByteTag set(int index, ByteTag element) {
        byte[] data = ref.getBytes();

        byte b0 = data[index];
        data[index] = element.getAsByte();
        return AuroraByteTag.of(b0);
    }

    @Override
    public void add(int index, ByteTag element) {
        ref.data = ArrayUtils.add(ref.getBytes(), index, element.getAsByte());
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            byte[] data = ref.getBytes();
            data[index] = ((NumericTag) tag).getAsByte();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            ref.data = ArrayUtils.add(ref.getBytes(), index, ((NumericTag) tag).getAsByte());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ByteTag remove(int index) {
        byte[] data = ref.getBytes();
        byte b0 = data[index];
        ref.data = ArrayUtils.remove(data, index);
        return AuroraByteTag.of(b0);
    }

    @Override
    public void clear() {
        ref.data = new byte[0];
    }

    @Override
    public AuroraByteArrayTag copy() {
        return new AuroraByteArrayTag(ArrayUtils.clone(ref.getBytes()));
    }

    @Override
    public String toString() {
        return ref.toString();
    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ByteArrayTag) {
            if (o instanceof AuroraByteArrayTag) {
                return ref.equals(((AuroraByteArrayTag) o).ref);
            } else {
                return Arrays.equals(getByteArray(), ((ByteArrayTag) o).getByteArray());
            }
        } else {
            return false;
        }
    }
}
