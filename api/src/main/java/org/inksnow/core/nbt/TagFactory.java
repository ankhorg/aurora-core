package org.inksnow.core.nbt;

import org.inksnow.core.Aurora;

import java.util.List;
import java.util.function.Supplier;

/**
 * A factory for creating NBT tags.
 */
public interface TagFactory {
    Supplier<TagFactory> INSTANCE = Aurora.getFactoryLazy(TagFactory.class);

    /**
     * Get the instance of the TagFactory.
     *
     * @return the instance of the TagFactory
     */
    static TagFactory instance() {
        return INSTANCE.get();
    }

    /**
     * Wrap a Minecraft NBT tag.
     *
     * @param minecraftTag the Minecraft NBT tag
     * @return the wrapped tag
     */
    Tag wrap(Object minecraftTag);

    /**
     * Unwrap a wrapped tag.
     *
     * @param tag the wrapped tag
     * @return the Minecraft NBT tag
     */
    Object unwrap(Tag tag);

    /**
     * Create a new byte tag.
     *
     * @param value the value
     * @return the byte tag
     */
    ByteTag byteTag(byte value);

    /**
     * Create a new byte tag.
     *
     * @param value the value
     * @return the byte tag
     */
    ByteTag byteTag(boolean value);

    /**
     * Create a new short tag.
     *
     * @param value the value
     * @return the short tag
     */
    ShortTag shortTag(short value);

    /**
     * Create a new int tag.
     *
     * @param value the value
     * @return the int tag
     */
    IntTag intTag(int value);

    /**
     * Create a new long tag.
     *
     * @param value the value
     * @return the long tag
     */
    LongTag longTag(long value);

    /**
     * Create a new float tag.
     *
     * @param value the value
     * @return the float tag
     */
    FloatTag floatTag(float value);

    /**
     * Create a new double tag.
     *
     * @param value the value
     * @return the double tag
     */
    DoubleTag doubleTag(double value);

    /**
     * Create a new byte array tag.
     *
     * @param value the value
     * @return the byte array tag
     */
    ByteArrayTag byteArrayTag(byte[] value);

    /**
     * Create a new byte array tag.
     *
     * @param value the value
     * @return the byte array tag
     */
    ByteArrayTag byteArrayTag(List<Byte> value);

    /**
     * Create a new string tag.
     *
     * @param value the value
     * @return the string tag
     */
    StringTag stringTag(String value);

    /**
     * Create a new list tag.
     *
     * @return the list tag
     */
    ListTag listTag();

    /**
     * Create a new compound tag.
     *
     * @return the compound tag
     */
    CompoundTag compoundTag();

    /**
     * Create a new int array tag.
     *
     * @param value the value
     * @return the int array tag
     */
    IntArrayTag intArrayTag(int[] value);

    /**
     * Create a new int array tag.
     *
     * @param value the value
     * @return the int array tag
     */
    IntArrayTag intArrayTag(List<Integer> value);

    /**
     * Create a new long array tag.
     *
     * @param value the value
     * @return the long array tag
     */
    LongArrayTag longArrayTag(long[] value);

    /**
     * Create a new long array tag.
     *
     * @param value the value
     * @return the long array tag
     */
    LongArrayTag longArrayTag(List<Long> value);
}
