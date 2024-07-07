package org.inksnow.core.impl.nbt;

import org.apache.commons.lang.ArrayUtils;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.ref.nbt.RefNbtTagDouble;
import org.inksnow.core.impl.ref.nbt.RefNbtTagFloat;
import org.inksnow.core.impl.ref.nbt.RefNbtTagInt;
import org.inksnow.core.impl.ref.nbt.RefNbtTagIntArray;
import org.inksnow.core.impl.ref.nbt.RefNbtTagList;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLongArray;
import org.inksnow.core.impl.ref.nbt.RefNbtTagShort;
import org.inksnow.core.nbt.CompoundTag;
import org.inksnow.core.nbt.ListTag;
import org.inksnow.core.nbt.Tag;

import java.util.Locale;

public final class AuroraListTag extends AuroraCollectionTag<RefNbtBase, Tag> implements ListTag {
    private static final boolean SET2_SUPPORT = CraftBukkitVersion.v1_17_R1.isSupport();
    private static final boolean SET1_SUPPORT = CraftBukkitVersion.v1_13_R1.isSupport();

    private static final boolean ADD3_SUPPORT = CraftBukkitVersion.v1_14_R1.isSupport();
    private static final boolean ADD2_SUPPORT = CraftBukkitVersion.v1_14_R1.isSupport();
    private static final boolean ADD1_SUPPORT = CraftBukkitVersion.v1_13_R1.isSupport() && !CraftBukkitVersion.v1_14_R1.isSupport();
    private static final boolean ADD0_SUPPORT = CraftBukkitVersion.v1_12_R1.isSupport() && !CraftBukkitVersion.v1_13_R1.isSupport();

    private final RefNbtTagList ref;

    /* package-private */ AuroraListTag(RefNbtTagList ref) {
        this.ref = ref;
    }

    public AuroraListTag() {
        this(new RefNbtTagList());
    }

    @Override
    public byte getElementType() {
        if (ref.size() == 0) {
            return 0;
        } else {
            return ref.get(0).getTypeId();
        }
    }

    @Override
    public RefNbtBase impl$ref() {
        return ref;
    }

    @Override
    public int size() {
        return ref.size();
    }

    @Override
    public CompoundTag getCompound(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_COMPOUND) {
                return new AuroraCompoundTag((RefNbtTagCompound) nbt);
            }
        }
        return new AuroraCompoundTag();
    }

    @Override
    public ListTag getList(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_LIST) {
                return new AuroraListTag((RefNbtTagList) nbt);
            }
        }
        return new AuroraListTag(new RefNbtTagList());
    }

    @Override
    public short getShort(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_SHORT) {
                return ((RefNbtTagShort) nbt).asShort();
            }
        }
        return 0;
    }

    @Override
    public int getInt(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_INT) {
                return ((RefNbtTagInt) nbt).asInt();
            }
        }
        return 0;
    }

    @Override
    public int[] getIntArray(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_INT_ARRAY) {
                return ((RefNbtTagIntArray) nbt).getInts();
            }
        }
        return ArrayUtils.EMPTY_INT_ARRAY;
    }

    @Override
    public long[] getLongArray(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_LONG_ARRAY) {
                return ((RefNbtTagLongArray) nbt).getLongs();
            }
        }
        return ArrayUtils.EMPTY_LONG_ARRAY;
    }

    @Override
    public double getDouble(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_DOUBLE) {
                return ((RefNbtTagDouble) nbt).asDouble();
            }
        }
        return 0;
    }

    @Override
    public float getFloat(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            if (nbt.getTypeId() == Tag.TAG_FLOAT) {
                return ((RefNbtTagFloat) nbt).asFloat();
            }
        }
        return 0;
    }

    @Override
    public String getString(int index) {
        if (index >= 0 && index < size()) {
            RefNbtBase nbt = ref.get(index);
            return nbt.getTypeId() == Tag.TAG_STRING ? nbt.asString() : nbt.toString();
        } else {
            return "";
        }
    }

    @Override
    public Tag get(int index) {
        return AuroraTagFactory.INSTANCE.wrap(ref.get(index));
    }

    @Override
    public boolean add(Tag tag) {
        if (ADD0_SUPPORT) {
            if (updateType(tag)) {
                ref.add0(AuroraTagFactory.INSTANCE.unwrapImpl(tag));
                return true;
            } else {
                throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", tag.getId(), getElementType()));
            }
        } else if (ADD1_SUPPORT) {
            if (ref.add1(AuroraTagFactory.INSTANCE.unwrapImpl(tag))) {
                return true;
            } else {
                throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", tag.getId(), getElementType()));
            }
        } else {
            return super.add(tag);
        }
    }

    @Override
    public Tag set(int index, Tag element) {
        if (SET1_SUPPORT) {
            return AuroraTagFactory.INSTANCE.wrap(
                    ref.set1(index, AuroraTagFactory.INSTANCE.unwrapImpl(element))
            );
        } else {
            Tag old = get(index);
            if (!this.setTag(index, element)) {
                throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", element.getId(), getElementType()));
            } else {
                return old;
            }
        }
    }

    @Override
    public void add(int index, Tag element) {
        if (ADD2_SUPPORT) {
            ref.add2(index, AuroraTagFactory.INSTANCE.unwrapImpl(element));
        } else {
            if (!this.addTag(index, element)) {
                throw new UnsupportedOperationException(String.format(Locale.ROOT, "Trying to add tag of type %d to list of %d", element.getId(), getElementType()));
            }
        }
    }

    private boolean updateType(Tag tag) {
        byte tagId = tag.getId();
        if (tagId == 0) {
            return false;
        }
        byte elementId = getElementType();
        if (elementId == 0) {
            return true;
        } else {
            return elementId == tag.getId();
        }
    }

    @Override
    public boolean setTag(int index, Tag tag) {
        if (SET2_SUPPORT) {
            return ref.set2(index, AuroraTagFactory.INSTANCE.unwrapImpl(tag));
        }
        if (updateType(tag)) {
            if (SET1_SUPPORT) {
                ref.set1(index, AuroraTagFactory.INSTANCE.unwrapImpl(tag));
            } else {
                ref.set0(index, AuroraTagFactory.INSTANCE.unwrapImpl(tag));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (ADD3_SUPPORT) {
            return ref.add3(index, AuroraTagFactory.INSTANCE.unwrapImpl(tag));
        } else {
            if (updateType(tag)) {
                ref.list.add(index, AuroraTagFactory.INSTANCE.unwrapImpl(tag));
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public Tag remove(int index) {
        return AuroraTagFactory.INSTANCE.wrap(
                ref.remove(index)
        );
    }

    @Override
    public AuroraListTag copy() {
        return new AuroraListTag((RefNbtTagList) ref.rClone());
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
        if (o instanceof ListTag) {
            if (o instanceof AuroraListTag) {
                return ref.equals(((AuroraListTag) o).ref);
            } else {
                ListTag other = (ListTag) o;
                if (size() != other.size()) {
                    return false;
                }
                for (int i = 0; i < size(); i++) {
                    Tag tag = get(i);
                    Tag otherTag = other.get(i);
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
