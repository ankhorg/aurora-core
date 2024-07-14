package org.inksnow.core.impl.nbt;

import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagByte;
import org.inksnow.core.impl.ref.nbt.RefNbtTagByteArray;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.ref.nbt.RefNbtTagDouble;
import org.inksnow.core.impl.ref.nbt.RefNbtTagFloat;
import org.inksnow.core.impl.ref.nbt.RefNbtTagInt;
import org.inksnow.core.impl.ref.nbt.RefNbtTagIntArray;
import org.inksnow.core.impl.ref.nbt.RefNbtTagList;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLong;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLongArray;
import org.inksnow.core.impl.ref.nbt.RefNbtTagShort;
import org.inksnow.core.impl.ref.nbt.RefNbtTagString;
import org.inksnow.core.nbt.ByteArrayTag;
import org.inksnow.core.nbt.ByteTag;
import org.inksnow.core.nbt.CompoundTag;
import org.inksnow.core.nbt.DoubleTag;
import org.inksnow.core.nbt.FloatTag;
import org.inksnow.core.nbt.IntArrayTag;
import org.inksnow.core.nbt.IntTag;
import org.inksnow.core.nbt.ListTag;
import org.inksnow.core.nbt.LongArrayTag;
import org.inksnow.core.nbt.LongTag;
import org.inksnow.core.nbt.ShortTag;
import org.inksnow.core.nbt.StringTag;
import org.inksnow.core.nbt.Tag;
import org.inksnow.core.nbt.TagFactory;

import java.util.List;

public final class AuroraTagFactory implements TagFactory {
    public static final AuroraTagFactory INSTANCE = new AuroraTagFactory();

    private AuroraTagFactory() {
        //
    }

    /* package-private */ RefNbtBase unwrapImpl(Tag tag) {
        if (tag instanceof AuroraTag) {
            return ((AuroraTag<?>) tag).impl$ref();
        } else {
            switch (tag.getId()) {
                case Tag.TAG_BYTE:
                    return unwrapImpl(AuroraByteTag.of(((ByteTag) tag).getAsByte()));
                case Tag.TAG_SHORT:
                    return unwrapImpl(AuroraShortTag.of(((ShortTag) tag).getAsShort()));
                case Tag.TAG_INT:
                    return unwrapImpl(AuroraIntTag.of(((IntTag) tag).getAsInt()));
                case Tag.TAG_LONG:
                    return unwrapImpl(AuroraLongTag.of(((LongTag) tag).getAsLong()));
                case Tag.TAG_FLOAT:
                    return new RefNbtTagFloat(((FloatTag) tag).getAsFloat());
                case Tag.TAG_DOUBLE:
                    return new RefNbtTagDouble(((DoubleTag) tag).getAsDouble());
                case Tag.TAG_BYTE_ARRAY:
                    return new RefNbtTagByteArray(((ByteArrayTag) tag).getByteArray());
                case Tag.TAG_STRING:
                    return new RefNbtTagString(tag.getAsString());
                case Tag.TAG_LIST: {
                    AuroraListTag newList = new AuroraListTag();
                    ListTag list = (ListTag) tag;
                    newList.addAll(list);
                    return unwrapImpl(newList);
                }
                case Tag.TAG_COMPOUND: {
                    AuroraCompoundTag newCompound = new AuroraCompoundTag();
                    CompoundTag compound = (CompoundTag) tag;
                    for (String key : compound.getAllKeys()) {
                        newCompound.put(key, compound.get(key));
                    }
                    return unwrapImpl(newCompound);
                }
                case Tag.TAG_INT_ARRAY:
                    return new RefNbtTagIntArray(((IntArrayTag) tag).getIntArray());
                case Tag.TAG_LONG_ARRAY:
                    return new RefNbtTagLongArray(((LongArrayTag) tag).getLongArray());
                default:
                    throw new UnsupportedOperationException("Unsupported tag type: " + tag.getId());
            }
        }
    }

    /* package-private */ Tag wrap(RefNbtBase base) {
        if (base == null) {
            return null;
        }
        switch (base.getTypeId()) {
            case Tag.TAG_BYTE:
                return AuroraByteTag.of(((RefNbtTagByte) base).asByte());
            case Tag.TAG_SHORT:
                return AuroraShortTag.wrap((RefNbtTagShort) base);
            case Tag.TAG_INT:
                return AuroraIntTag.wrap((RefNbtTagInt) base);
            case Tag.TAG_LONG:
                return AuroraLongTag.wrap((RefNbtTagLong) base);
            case Tag.TAG_FLOAT:
                return new AuroraFloatTag((RefNbtTagFloat) base);
            case Tag.TAG_DOUBLE:
                return new AuroraDoubleTag((RefNbtTagDouble) base);
            case Tag.TAG_BYTE_ARRAY:
                return new AuroraByteArrayTag((RefNbtTagByteArray) base);
            case Tag.TAG_STRING:
                return AuroraStringTag.wrap((RefNbtTagString) base);
            case Tag.TAG_LIST:
                return new AuroraListTag((RefNbtTagList) base);
            case Tag.TAG_COMPOUND:
                return new AuroraCompoundTag((RefNbtTagCompound) base);
            case Tag.TAG_INT_ARRAY:
                return new AuroraIntArrayTag((RefNbtTagIntArray) base);
            case Tag.TAG_LONG_ARRAY:
                return new AuroraLongArrayTag((RefNbtTagLongArray) base);
            default:
                throw new UnsupportedOperationException("Unsupported tag type: " + base.getTypeId());
        }
    }

    @Override
    public Object unwrap(Tag tag) {
        return unwrapImpl(tag);
    }

    @Override
    public Tag wrap(Object minecraftTag) {
        if (minecraftTag instanceof RefNbtBase) {
            return wrap((RefNbtBase) minecraftTag);
        } else {
            throw new UnsupportedOperationException("Unsupported tag type: " + minecraftTag.getClass());
        }
    }

    @Override
    public ByteTag byteTag(byte value) {
        return AuroraByteTag.of(value);
    }

    @Override
    public ByteTag byteTag(boolean value) {
        return AuroraByteTag.of(value);
    }

    @Override
    public ShortTag shortTag(short value) {
        return AuroraShortTag.of(value);
    }

    @Override
    public IntTag intTag(int value) {
        return AuroraIntTag.of(value);
    }

    @Override
    public LongTag longTag(long value) {
        return AuroraLongTag.of(value);
    }

    @Override
    public FloatTag floatTag(float value) {
        return new AuroraFloatTag(value);
    }

    @Override
    public DoubleTag doubleTag(double value) {
        return new AuroraDoubleTag(value);
    }

    @Override
    public ByteArrayTag byteArrayTag(byte[] value) {
        return new AuroraByteArrayTag(value);
    }

    @Override
    public ByteArrayTag byteArrayTag(List<Byte> value) {
        return new AuroraByteArrayTag(value);
    }

    @Override
    public StringTag stringTag(String value) {
        return AuroraStringTag.of(value);
    }

    @Override
    public ListTag listTag() {
        return new AuroraListTag();
    }

    @Override
    public CompoundTag compoundTag() {
        return new AuroraCompoundTag();
    }

    @Override
    public IntArrayTag intArrayTag(int[] value) {
        return new AuroraIntArrayTag(value);
    }

    @Override
    public IntArrayTag intArrayTag(List<Integer> value) {
        return new AuroraIntArrayTag(value);
    }

    @Override
    public LongArrayTag longArrayTag(long[] value) {
        return new AuroraLongArrayTag(value);
    }

    @Override
    public LongArrayTag longArrayTag(List<Long> value) {
        return new AuroraLongArrayTag(value);
    }
}
