package org.inksnow.core.nbt;

import org.inksnow.core.Aurora;

import java.util.List;

public interface TagFactory {
    /**
     * Get the instance of the TagFactory.
     *
     * @return the instance of the TagFactory
     */
    static TagFactory instance() {
        return Aurora.getFactory(TagFactory.class);
    }

    Tag wrap(Object minecraftTag);

    Object unwrap(Tag tag);

    ByteTag byteTag(byte value);

    ByteTag byteTag(boolean value);

    ShortTag shortTag(short value);

    IntTag intTag(int value);

    LongTag longTag(long value);

    FloatTag floatTag(float value);

    DoubleTag doubleTag(double value);

    ByteArrayTag byteArrayTag(byte[] value);

    ByteArrayTag byteArrayTag(List<Byte> value);

    StringTag stringTag(String value);

    ListTag listTag();

    CompoundTag compoundTag();

    IntArrayTag intArrayTag(int[] value);

    IntArrayTag intArrayTag(List<Integer> value);

    LongArrayTag longArrayTag(long[] value);

    LongArrayTag longArrayTag(List<Long> value);
}
