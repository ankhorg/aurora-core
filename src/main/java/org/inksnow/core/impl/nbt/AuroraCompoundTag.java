package org.inksnow.core.impl.nbt;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.ref.nbt.RefNbtTagIntArray;
import org.inksnow.core.impl.util.ArrayUtil;
import org.inksnow.core.nbt.ByteTag;
import org.inksnow.core.nbt.CompoundTag;
import org.inksnow.core.nbt.ListTag;
import org.inksnow.core.nbt.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AuroraCompoundTag implements AuroraTag<RefNbtTagCompound>, CompoundTag {
    private static final boolean SET1_SUPPORT = CraftBukkitVersion.v1_14_R1.isSupport();
    private static final boolean PUT_LIST_SUPPORT = CraftBukkitVersion.v1_17_R1.isSupport();

    private final RefNbtTagCompound ref;

    /* package-private */ AuroraCompoundTag(RefNbtTagCompound ref) {
        this.ref = ref;
    }

    public AuroraCompoundTag() {
        this(new RefNbtTagCompound());
    }

    @Override
    public RefNbtTagCompound impl$ref() {
        return ref;
    }

    @Override
    public Set<String> getAllKeys() {
        return ref.getKeys();
    }

    @Override
    public int size() {
        return ref.size();
    }

    @Override
    public @Nullable Tag put(String key, Tag element) {
        if (SET1_SUPPORT) {
            return AuroraTagFactory.INSTANCE.wrap(ref.set1(key, AuroraTagFactory.INSTANCE.unwrapImpl(element)));
        } else {
            final @Nullable RefNbtBase old = ref.get(key);
            ref.set0(key, AuroraTagFactory.INSTANCE.unwrapImpl(element));
            return AuroraTagFactory.INSTANCE.wrap(old);
        }
    }

    @Override
    public void putByte(String key, byte value) {
        ref.setByte(key, value);
    }

    @Override
    public void putShort(String key, short value) {
        ref.setShort(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        ref.setInt(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        ref.setLong(key, value);
    }

    @Override
    public void putUUID(String key, UUID value) {
        ref.setUUID(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        ref.setFloat(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        ref.setDouble(key, value);
    }

    @Override
    public void putString(String key, String value) {
        ref.setString(key, value);
    }

    @Override
    public void putByteArray(String key, List<Byte> value) {
        if (PUT_LIST_SUPPORT) {
            ref.setByteArray(key, value);
        } else {
            ref.setByteArray(key, ArrayUtil.byteListToArray(value));
        }
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        ref.setByteArray(key, value);
    }

    @Override
    public void putIntArray(String key, int[] value) {
        ref.setIntArray(key, value);
    }

    @Override
    public void putIntArray(String key, List<Integer> value) {
        if (PUT_LIST_SUPPORT) {
            ref.setIntArray(key, value);
        } else {
            ref.setIntArray(key, ArrayUtil.intListToArray(value));
        }
    }

    @Override
    public void putLongArray(String key, List<Long> value) {
        if (PUT_LIST_SUPPORT) {
            ref.setLongArray(key, value);
        } else {
            ref.setLongArray(key, ArrayUtil.longListToArray(value));
        }
    }

    @Override
    public void putLongArray(String key, long[] value) {
        ref.setLongArray(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        ref.setBoolean(key, value);
    }

    @Override
    public Tag get(String key) {
        return AuroraTagFactory.INSTANCE.wrap(ref.get(key));
    }

    @Override
    public byte getTagType(String key) {
        return ref.getType(key);
    }

    @Override
    public boolean contains(String key, byte type) {
        return ref.hasKeyOfType(key, type);
    }

    @Override
    public boolean hasUUID(String key) {
        if (ref.hasKeyOfType(key + "Most", Tag.TAG_ANY_NUMERIC) && ref.hasKeyOfType(key + "Least", Tag.TAG_ANY_NUMERIC)) {
            return true;
        }

        final @Nullable RefNbtBase tag = ref.get(key);
        return tag != null
                && tag.getTypeId() == Tag.TAG_INT_ARRAY
                && ((RefNbtTagIntArray) tag).getInts().length == 4;
    }

    @Override
    public boolean contains(String key) {
        return ref.hasKey(key);
    }

    @Override
    public byte getByte(String key) {
        return ref.getByte(key);
    }

    @Override
    public short getShort(String key) {
        return ref.getShort(key);
    }

    @Override
    public int getInt(String key) {
        return ref.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return ref.getLong(key);
    }

    private static UUID uuidFromIntArray(int[] array) {
        return new UUID((long)array[0] << 32 | (long)array[1] & 4294967295L, (long)array[2] << 32 | (long)array[3] & 4294967295L);
    }

    @Override
    public UUID getUUID(String key) {
        // compatibility with old versions
        if (!contains(key, Tag.TAG_INT_ARRAY) && contains(key + "Most", Tag.TAG_ANY_NUMERIC) && contains(key + "Least", Tag.TAG_ANY_NUMERIC)) {
           return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
        }
        // try new format
        final RefNbtBase tag = ref.get(key);
        if (tag != null && tag.getTypeId() == Tag.TAG_INT_ARRAY) {
           RefNbtTagIntArray uuidTag = (RefNbtTagIntArray) tag;
           int[] array = uuidTag.getInts();
           if (array.length == 4) {
                return uuidFromIntArray(array);
           }
        }
        // use current version
        return ref.getUUID(key);
    }

    @Override
    public float getFloat(String key) {
        return ref.getFloat(key);
    }

    @Override
    public double getDouble(String key) {
        return ref.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return ref.getString(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return ref.getByteArray(key);
    }

    @Override
    public int[] getIntArray(String key) {
        return ref.getIntArray(key);
    }

    @Override
    public long[] getLongArray(String key) {
        return ref.getLongArray(key);
    }

    @Override
    public CompoundTag getCompound(String key) {
        return new AuroraCompoundTag(ref.getCompound(key));
    }

    @Override
    public ListTag getList(String key, int type) {
        return new AuroraListTag(ref.getList(key, type));
    }

    @Override
    public boolean getBoolean(String key) {
        return ref.getBoolean(key);
    }

    @Override
    public void remove(String key) {
        ref.remove(key);
    }

    @Override
    public boolean isEmpty() {
        return ref.isEmpty();
    }

    @Override
    public Tag copy() {
        return new AuroraCompoundTag((RefNbtTagCompound) ref.rClone());
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
        if (o instanceof CompoundTag) {
            if (o instanceof AuroraCompoundTag) {
                return ref.equals(((AuroraCompoundTag) o).ref);
            } else {
                CompoundTag other = (CompoundTag) o;
                Set<String> keys = getAllKeys();
                Set<String> otherKeys = other.getAllKeys();
                if (keys.size() != otherKeys.size() || !keys.containsAll(otherKeys)) {
                    return false;
                }
                for (String key : keys) {
                    Tag tag = get(key);
                    Tag otherTag = other.get(key);
                    if (!tag.equals(otherTag)) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }
}
