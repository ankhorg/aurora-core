package org.inksnow.core.impl.nbt;

import org.apache.commons.lang.ArrayUtils;
import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagInt;
import org.inksnow.core.impl.ref.nbt.RefNbtTagIntArray;
import org.inksnow.core.nbt.ByteArrayTag;
import org.inksnow.core.nbt.IntTag;
import org.inksnow.core.nbt.IntArrayTag;
import org.inksnow.core.nbt.NumericTag;
import org.inksnow.core.nbt.Tag;

import java.util.Arrays;
import java.util.List;

public final class AuroraIntArrayTag extends AuroraCollectionTag<RefNbtTagInt, IntTag> implements IntArrayTag {
    private final RefNbtTagIntArray ref;

    /* package-private */ AuroraIntArrayTag(RefNbtTagIntArray ref) {
        this.ref = ref;
    }

    public AuroraIntArrayTag(int[] data) {
        this(new RefNbtTagIntArray(data));
    }

    public AuroraIntArrayTag(List<Integer> data) {
        this(new RefNbtTagIntArray(data));
    }

    @Override
    public RefNbtBase impl$ref() {
        return ref;
    }

    @Override
    public int[] getIntArray() {
        return ref.getInts();
    }

    @Override
    public int size() {
        return ref.getInts().length;
    }

    @Override
    public IntTag get(int index) {
        return AuroraIntTag.of(ref.getInts()[index]);
    }

    @Override
    public IntTag set(int index, IntTag element) {
        int[] data = ref.getInts();

        int b0 = data[index];
        data[index] = element.getAsInt();
        return AuroraIntTag.of(b0);
    }

    @Override
    public void add(int index, IntTag element) {
        ref.data = ArrayUtils.add(ref.getInts(), index, element.getAsInt());
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            int[] data = ref.getInts();
            data[index] = ((NumericTag) tag).getAsInt();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            ref.data = ArrayUtils.add(ref.getInts(), index, ((NumericTag) tag).getAsInt());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IntTag remove(int index) {
        int[] data = ref.getInts();
        int b0 = data[index];
        ref.data = ArrayUtils.remove(data, index);
        return AuroraIntTag.of(b0);
    }

    @Override
    public void clear() {
        ref.data = new int[0];
    }

    @Override
    public AuroraIntArrayTag copy() {
        return new AuroraIntArrayTag(ArrayUtils.clone(ref.getInts()));
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
        if (o instanceof IntArrayTag) {
            if (o instanceof AuroraIntArrayTag) {
                return ref.equals(((AuroraIntArrayTag) o).ref);
            } else {
                return Arrays.equals(ref.getInts(), ((IntArrayTag) o).getIntArray());
            }
        } else {
            return false;
        }
    }
}
