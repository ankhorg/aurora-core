package org.inksnow.core.impl.nbt;

import org.apache.commons.lang.ArrayUtils;
import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLong;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLongArray;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLong;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLongArray;
import org.inksnow.core.nbt.LongArrayTag;
import org.inksnow.core.nbt.LongTag;
import org.inksnow.core.nbt.LongArrayTag;
import org.inksnow.core.nbt.LongTag;
import org.inksnow.core.nbt.NumericTag;
import org.inksnow.core.nbt.Tag;

import java.util.Arrays;
import java.util.List;

public final class AuroraLongArrayTag extends AuroraCollectionTag<RefNbtTagLong, LongTag> implements LongArrayTag {
    private final RefNbtTagLongArray ref;

    /* package-private */ AuroraLongArrayTag(RefNbtTagLongArray ref) {
        this.ref = ref;
    }

    public AuroraLongArrayTag(long[] data) {
        this(new RefNbtTagLongArray(data));
    }

    public AuroraLongArrayTag(List<Long> data) {
        this(new RefNbtTagLongArray(data));
    }

    @Override
    public RefNbtBase impl$ref() {
        return ref;
    }

    @Override
    public long[] getLongArray() {
        return ref.getLongs();
    }

    @Override
    public int size() {
        return ref.getLongs().length;
    }

    @Override
    public LongTag get(int index) {
        return AuroraLongTag.of(ref.getLongs()[index]);
    }

    @Override
    public LongTag set(int index, LongTag element) {
        long[] data = ref.getLongs();

        long b0 = data[index];
        data[index] = element.getAsLong();
        return AuroraLongTag.of(b0);
    }

    @Override
    public void add(int index, LongTag element) {
        ref.data = ArrayUtils.add(ref.getLongs(), index, element.getAsLong());
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            long[] data = ref.getLongs();
            data[index] = ((NumericTag) tag).getAsLong();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof NumericTag) {
            ref.data = ArrayUtils.add(ref.getLongs(), index, ((NumericTag) tag).getAsLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public LongTag remove(int index) {
        long[] data = ref.getLongs();
        long b0 = data[index];
        ref.data = ArrayUtils.remove(data, index);
        return AuroraLongTag.of(b0);
    }

    @Override
    public void clear() {
        ref.data = new long[0];
    }

    @Override
    public AuroraLongArrayTag copy() {
        return new AuroraLongArrayTag(ArrayUtils.clone(ref.getLongs()));
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
        if (o instanceof LongArrayTag) {
            if (o instanceof AuroraLongArrayTag) {
                return ref.equals(((AuroraLongArrayTag) o).ref);
            } else {
                return Arrays.equals(ref.getLongs(), ((LongArrayTag) o).getLongArray());
            }
        } else {
            return false;
        }
    }
}
