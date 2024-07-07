package org.inksnow.core.nbt;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CompoundTag extends Tag {
    Set<String> getAllKeys();

    default byte getId() {
        return Tag.TAG_COMPOUND;
    }

    int size();

    @Nullable
    Tag put(String key, Tag element);

    void putByte(String key, byte value);

    void putShort(String key, short value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putUUID(String key, UUID value);

    void putFloat(String key, float value);

    void putDouble(String key, double value);

    void putString(String key, String value);

    void putByteArray(String key, byte[] value);

    void putByteArray(String key, List<Byte> value);

    void putIntArray(String key, int[] value);

    void putIntArray(String key, List<Integer> value);

    void putLongArray(String key, long[] value);

    void putLongArray(String key, List<Long> value);

    void putBoolean(String key, boolean value);

    Tag get(String key);

    byte getTagType(String key);

    boolean contains(String key);

    boolean contains(String key, byte type);

    boolean hasUUID(String key);

    byte getByte(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    /**
     * You must use {@link #hasUUID(String)} before or else it <b>will</b> throw an NPE.
     *
     * @param key The key to get the UUID from
     * @return The UUID
     */
    UUID getUUID(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getString(String key);

    byte[] getByteArray(String key);

    int[] getIntArray(String key);

    long[] getLongArray(String key);

    CompoundTag getCompound(String key);

    ListTag getList(String key, int type);

    boolean getBoolean(String key);

    void remove(String key);

    boolean isEmpty();

    static CompoundTag create() {
        return TagFactory.instance().compoundTag();
    }
}
