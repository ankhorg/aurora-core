package org.inksnow.core.impl.data.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.exception.InvalidDataException;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataSerializable;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.impl.nbt.AuroraByteArrayTag;
import org.inksnow.core.impl.nbt.AuroraByteTag;
import org.inksnow.core.impl.nbt.AuroraCompoundTag;
import org.inksnow.core.impl.nbt.AuroraDoubleTag;
import org.inksnow.core.impl.nbt.AuroraFloatTag;
import org.inksnow.core.impl.nbt.AuroraIntArrayTag;
import org.inksnow.core.impl.nbt.AuroraIntTag;
import org.inksnow.core.impl.nbt.AuroraListTag;
import org.inksnow.core.impl.nbt.AuroraLongArrayTag;
import org.inksnow.core.impl.nbt.AuroraLongTag;
import org.inksnow.core.impl.nbt.AuroraShortTag;
import org.inksnow.core.impl.nbt.AuroraStringTag;
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
import org.inksnow.core.nbt.Tag;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class NBTTranslator implements DataTranslator<CompoundTag> {

    public static final NBTTranslator INSTANCE = new NBTTranslator();

    private static final TypeToken<CompoundTag> TOKEN = TypeToken.get(CompoundTag.class);
    public static final String BOOLEAN_IDENTIFIER = "$Boolean";

    private static CompoundTag containerToCompound(final DataView container) {
        Objects.requireNonNull(container);
        CompoundTag compound = new AuroraCompoundTag();
        NBTTranslator.containerToCompound(container, compound);
        return compound;
    }

    private static void containerToCompound(final DataView container, final CompoundTag compound) {
        // We don't need to get deep values since all nested DataViews will be found
        // from the instance of checks.
        Objects.requireNonNull(container);
        Objects.requireNonNull(compound);
        for (Map.Entry<DataQuery, Object> entry : container.values(false).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey().asString('.');
            if (value instanceof DataView) {
                CompoundTag inner = new AuroraCompoundTag();
                NBTTranslator.containerToCompound(container.getView(entry.getKey()).get(), inner);
                compound.put(key, inner);
            } else if (value instanceof Boolean) {
                compound.put(key + NBTTranslator.BOOLEAN_IDENTIFIER, AuroraByteTag.of((Boolean) value));
            } else {
                compound.put(key, NBTTranslator.getBaseFromObject(value));
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Tag getBaseFromObject(final Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Boolean) {
            return AuroraByteTag.of((Boolean) value);
        } else if (value instanceof Byte) {
            return AuroraByteTag.of((Byte) value);
        } else if (value instanceof Short) {
            return AuroraShortTag.of((Short) value);
        } else if (value instanceof Integer) {
            return AuroraIntTag.of((Integer) value);
        } else if (value instanceof Long) {
            return AuroraLongTag.of((Long) value);
        } else if (value instanceof Float) {
            return AuroraFloatTag.of((Float) value);
        } else if (value instanceof Double) {
            return AuroraDoubleTag.of((Double) value);
        } else if (value instanceof String) {
            return AuroraStringTag.of((String) value);
        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                return new AuroraByteArrayTag((byte[]) value);
            } else if (value instanceof Byte[]) {
                byte[] array = new byte[((Byte[]) value).length];
                int counter = 0;
                for (Byte data : (Byte[]) value) {
                    array[counter++] = data;
                }
                return new AuroraByteArrayTag(array);
            } else if (value instanceof int[]) {
                return new AuroraIntArrayTag((int[]) value);
            } else if (value instanceof Integer[]) {
                int[] array = new int[((Integer[]) value).length];
                int counter = 0;
                for (Integer data : (Integer[]) value) {
                    array[counter++] = data;
                }
                return new AuroraIntArrayTag(array);
            } else if (value instanceof long[]) {
                return new AuroraLongArrayTag((long[]) value);
            } else if (value instanceof Long[]) {
                long[] array = new long[((Long[]) value).length];
                int counter = 0;
                for (Long data : (Long[]) value) {
                    array[counter++] = data;
                }
                return new AuroraLongArrayTag(array);
            }
        } else if (value instanceof List) {
            AuroraListTag list = new AuroraListTag();
            for (Object object : (List) value) {
                // Oh hey, we already have a translation already
                // since DataView only supports some primitive types anyways...
                list.add(NBTTranslator.getBaseFromObject(object));
            }
            return list;
        } else if (value instanceof Map) {
            AuroraCompoundTag compound = new AuroraCompoundTag();
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                if (entry.getKey() instanceof DataQuery) {
                    if (entry.getValue() instanceof Boolean) {
                        compound.putBoolean(((DataQuery) entry.getKey()).asString('.') + NBTTranslator.BOOLEAN_IDENTIFIER, (Boolean) entry.getValue());
                    } else {
                        compound.put(((DataQuery) entry.getKey()).asString('.'), NBTTranslator.getBaseFromObject(entry.getValue()));
                    }
                } else if (entry.getKey() instanceof String) {
                    compound.put((String) entry.getKey(), NBTTranslator.getBaseFromObject(entry.getValue()));
                } else {
                    compound.put(entry.getKey().toString(), NBTTranslator.getBaseFromObject(entry.getValue()));
                }
            }
            return compound;
        } else if (value instanceof DataSerializable) {
            return NBTTranslator.containerToCompound(((DataSerializable) value).toContainer());
        } else if (value instanceof DataView) {
            return NBTTranslator.containerToCompound((DataView) value);
        }
        throw new IllegalArgumentException("Unable to translate object to NBTBase: " + value);
    }

    private static DataContainer getViewFromCompound(CompoundTag compound) {
        Objects.requireNonNull(compound);
        DataContainer container = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        NBTTranslator.INSTANCE.addTo(compound, container);
        return container;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setInternal(Tag base, byte type, DataView view, String key) {
        Objects.requireNonNull(base);
        Objects.requireNonNull(view);
        Objects.requireNonNull(key);
        Preconditions.checkArgument(!key.isEmpty());
        Preconditions.checkArgument(type > Tag.TAG_END && type <= Tag.TAG_LONG_ARRAY);
        switch (type) {
            case Tag.TAG_BYTE:
                if (key.contains(NBTTranslator.BOOLEAN_IDENTIFIER)) {
                    view.set(DataQuery.of(key.replace(NBTTranslator.BOOLEAN_IDENTIFIER, "")), (((ByteTag) base).getAsByte() != 0));
                } else {
                    view.set(DataQuery.of(key), ((ByteTag) base).getAsByte());
                }
                break;
            case Tag.TAG_SHORT:
                view.set(DataQuery.of(key), ((ShortTag) base).getAsShort());
                break;
            case Tag.TAG_INT:
                view.set(DataQuery.of(key), ((IntTag) base).getAsInt());
                break;
            case Tag.TAG_LONG:
                view.set(DataQuery.of(key), ((LongTag) base).getAsLong());
                break;
            case Tag.TAG_FLOAT:
                view.set(DataQuery.of(key), ((FloatTag) base).getAsFloat());
                break;
            case Tag.TAG_DOUBLE:
                view.set(DataQuery.of(key), ((DoubleTag) base).getAsDouble());
                break;
            case Tag.TAG_BYTE_ARRAY:
                view.set(DataQuery.of(key), ((ByteArrayTag) base).getByteArray());
                break;
            case Tag.TAG_STRING:
                view.set(DataQuery.of(key), base.getAsString());
                break;
            case Tag.TAG_LIST:
                ListTag list = (ListTag) base;
                byte listType = list.getElementType();
                int count = list.size();
                List objectList = Lists.newArrayListWithCapacity(count);
                for (final Tag inbt : list) {
                    objectList.add(NBTTranslator.fromTagBase(inbt, listType));
                }
                view.set(DataQuery.of(key), objectList);
                break;
            case Tag.TAG_COMPOUND:
                DataView internalView = view.createView(DataQuery.of(key));
                CompoundTag compound = (CompoundTag) base;
                for (String internalKey : compound.getAllKeys()) {
                    Tag internalBase = compound.get(internalKey);
                    byte internalType = internalBase.getId();
                    // Basically.... more recursion.
                    // Reasoning: This avoids creating a new DataContainer which would
                    // then be copied in to the owning DataView anyways. We can internally
                    // set the actual data directly to the child view instead.
                    NBTTranslator.setInternal(internalBase, internalType, internalView, internalKey);
                }
                break;
            case Tag.TAG_INT_ARRAY:
                view.set(DataQuery.of(key), ((IntArrayTag) base).getIntArray());
                break;
            case Tag.TAG_LONG_ARRAY:
                view.set(DataQuery.of(key), ((LongArrayTag) base).getLongArray());
                break;
            default:
                throw new IllegalArgumentException("Unknown NBT type " + type);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object fromTagBase(Tag base, byte type) {
        switch (type) {
            case Tag.TAG_BYTE:
                return ((ByteTag) base).getAsByte();
            case Tag.TAG_SHORT:
                return (((ShortTag) base)).getAsShort();
            case Tag.TAG_INT:
                return ((IntTag) base).getAsInt();
            case Tag.TAG_LONG:
                return ((LongTag) base).getAsLong();
            case Tag.TAG_FLOAT:
                return ((FloatTag) base).getAsFloat();
            case Tag.TAG_DOUBLE:
                return ((DoubleTag) base).getAsDouble();
            case Tag.TAG_BYTE_ARRAY:
                return ((ByteArrayTag) base).getByteArray();
            case Tag.TAG_STRING:
                return base.getAsString();
            case Tag.TAG_LIST:
                ListTag list = (ListTag) base;
                byte listType = list.getElementType();
                int count = list.size();
                List objectList = Lists.newArrayListWithCapacity(count);
                for (Tag inbt : list) {
                    objectList.add(NBTTranslator.fromTagBase(inbt, listType));
                }
                return objectList;
            case Tag.TAG_COMPOUND:
                return NBTTranslator.getViewFromCompound((CompoundTag) base);
            case Tag.TAG_INT_ARRAY:
                return ((IntArrayTag) base).getIntArray();
            case Tag.TAG_LONG_ARRAY:
                return ((LongArrayTag) base).getLongArray();
            default :
                return null;
        }
    }

    public void translateContainerToData(CompoundTag node, DataView container) {
        NBTTranslator.containerToCompound(container, node);
    }

    public DataContainer translateFrom(CompoundTag node) {
        return NBTTranslator.getViewFromCompound(node);
    }

    @Override
    public TypeToken<CompoundTag> token() {
        return NBTTranslator.TOKEN;
    }

    @Override
    public CompoundTag translate(DataView view) throws InvalidDataException {
        return NBTTranslator.containerToCompound(view);
    }

    @Override
    public DataContainer translate(CompoundTag obj) throws InvalidDataException {
        return NBTTranslator.getViewFromCompound(obj);
    }

    @Override
    public DataView addTo(CompoundTag compound, DataView container) {
        for (String key : compound.getAllKeys()) {
            Tag base = compound.get(key);
            byte type = base.getId();
            NBTTranslator.setInternal(base, type, container, key); // gotta love recursion
        }
        return container;
    }
}
