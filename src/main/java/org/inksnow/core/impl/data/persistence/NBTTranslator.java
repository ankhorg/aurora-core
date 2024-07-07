package org.inksnow.core.impl.data.persistence;

import com.google.common.collect.Lists;
import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class NBTTranslator implements DataTranslator<RefNbtTagCompound> {

    public static final NBTTranslator INSTANCE = new NBTTranslator();

    private static final TypeToken<RefNbtTagCompound> TOKEN = TypeToken.get(RefNbtTagCompound.class);
    public static final String BOOLEAN_IDENTIFIER = "$Boolean";

    private static RefNbtTagCompound containerToCompound(final DataView container) {
        Objects.requireNonNull(container);
        RefNbtTagCompound compound = new RefNbtTagCompound();
        NBTTranslator.containerToCompound(container, compound);
        return compound;
    }

    private static void containerToCompound(final DataView container, final RefNbtTagCompound compound) {
        // We don't need to get deep values since all nested DataViews will be found
        // from the instance of checks.
        Objects.requireNonNull(container);
        Objects.requireNonNull(compound);
        for (Map.Entry<DataQuery, Object> entry : container.values(false).entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey().asString('.');
            if (value instanceof DataView) {
                RefNbtTagCompound inner = new RefNbtTagCompound();
                NBTTranslator.containerToCompound(container.getView(entry.getKey()).get(), inner);
                compound.put(key, inner);
            } else if (value instanceof Boolean) {
                compound.put(key + NBTTranslator.BOOLEAN_IDENTIFIER, ByteTag.valueOf((Boolean) value));
            } else {
                compound.put(key, NBTTranslator.getBaseFromObject(value));
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static RefNbtBase getBaseFromObject(final Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Boolean) {
            return ByteTag.valueOf((Boolean) value);
        } else if (value instanceof Byte) {
            return ByteTag.valueOf((Byte) value);
        } else if (value instanceof Short) {
            return ShortTag.valueOf((Short) value);
        } else if (value instanceof Integer) {
            return IntTag.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return LongTag.valueOf((Long) value);
        } else if (value instanceof Float) {
            return FloatTag.valueOf((Float) value);
        } else if (value instanceof Double) {
            return DoubleTag.valueOf((Double) value);
        } else if (value instanceof String) {
            return StringTag.valueOf((String) value);
        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                return new ByteArrayTag((byte[]) value);
            } else if (value instanceof Byte[]) {
                byte[] array = new byte[((Byte[]) value).length];
                int counter = 0;
                for (Byte data : (Byte[]) value) {
                    array[counter++] = data;
                }
                return new ByteArrayTag(array);
            } else if (value instanceof int[]) {
                return new IntArrayTag((int[]) value);
            } else if (value instanceof Integer[]) {
                int[] array = new int[((Integer[]) value).length];
                int counter = 0;
                for (Integer data : (Integer[]) value) {
                    array[counter++] = data;
                }
                return new IntArrayTag(array);
            } else if (value instanceof long[]) {
                return new LongArrayTag((long[]) value);
            } else if (value instanceof Long[]) {
                long[] array = new long[((Long[]) value).length];
                int counter = 0;
                for (Long data : (Long[]) value) {
                    array[counter++] = data;
                }
                return new LongArrayTag(array);
            }
        } else if (value instanceof List) {
            ListTag list = new ListTag();
            for (Object object : (List) value) {
                // Oh hey, we already have a translation already
                // since DataView only supports some primitive types anyways...
                list.add(NBTTranslator.getBaseFromObject(object));
            }
            return list;
        } else if (value instanceof Map) {
            RefNbtTagCompound compound = new RefNbtTagCompound();
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

    private static DataContainer getViewFromCompound(RefNbtTagCompound compound) {
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
        Preconditions.checkArgument(type > Constants.NBT.TAG_END && type <= Constants.NBT.TAG_LONG_ARRAY);
        switch (type) {
            case Constants.NBT.TAG_BYTE:
                if (key.contains(NBTTranslator.BOOLEAN_IDENTIFIER)) {
                    view.set(of(key.replace(NBTTranslator.BOOLEAN_IDENTIFIER, "")), (((ByteTag) base).getAsByte() != 0));
                } else {
                    view.set(of(key), ((ByteTag) base).getAsByte());
                }
                break;
            case Constants.NBT.TAG_SHORT:
                view.set(of(key), ((ShortTag) base).getAsShort());
                break;
            case Constants.NBT.TAG_INT:
                view.set(of(key), ((IntTag) base).getAsInt());
                break;
            case Constants.NBT.TAG_LONG:
                view.set(of(key), ((LongTag) base).getAsLong());
                break;
            case Constants.NBT.TAG_FLOAT:
                view.set(of(key), ((FloatTag) base).getAsFloat());
                break;
            case Constants.NBT.TAG_DOUBLE:
                view.set(of(key), ((DoubleTag) base).getAsDouble());
                break;
            case Constants.NBT.TAG_BYTE_ARRAY:
                view.set(of(key), ((ByteArrayTag) base).getAsByteArray());
                break;
            case Constants.NBT.TAG_STRING:
                view.set(of(key), base.getAsString());
                break;
            case Constants.NBT.TAG_LIST:
                ListTag list = (ListTag) base;
                byte listType = list.getElementType();
                int count = list.size();
                List objectList = Lists.newArrayListWithCapacity(count);
                for (final Tag inbt : list) {
                    objectList.add(NBTTranslator.fromTagBase(inbt, listType));
                }
                view.set(of(key), objectList);
                break;
            case Constants.NBT.TAG_COMPOUND:
                DataView internalView = view.createView(of(key));
                RefNbtTagCompound compound = (RefNbtTagCompound) base;
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
            case Constants.NBT.TAG_INT_ARRAY:
                view.set(of(key), ((IntArrayTag) base).getAsIntArray());
                break;
            case Constants.NBT.TAG_LONG_ARRAY:
                view.set(of(key), ((LongArrayTag) base).getAsLongArray());
                break;
            default:
                throw new IllegalArgumentException("Unknown NBT type " + type);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object fromTagBase(Tag base, byte type) {
        switch (type) {
            case Constants.NBT.TAG_BYTE:
                return ((ByteTag) base).getAsByte();
            case Constants.NBT.TAG_SHORT:
                return (((ShortTag) base)).getAsShort();
            case Constants.NBT.TAG_INT:
                return ((IntTag) base).getAsInt();
            case Constants.NBT.TAG_LONG:
                return ((LongTag) base).getAsLong();
            case Constants.NBT.TAG_FLOAT:
                return ((FloatTag) base).getAsFloat();
            case Constants.NBT.TAG_DOUBLE:
                return ((DoubleTag) base).getAsDouble();
            case Constants.NBT.TAG_BYTE_ARRAY:
                return ((ByteArrayTag) base).getAsByteArray();
            case Constants.NBT.TAG_STRING:
                return base.getAsString();
            case Constants.NBT.TAG_LIST:
                ListTag list = (ListTag) base;
                byte listType = list.getElementType();
                int count = list.size();
                List objectList = Lists.newArrayListWithCapacity(count);
                for (Tag inbt : list) {
                    objectList.add(NBTTranslator.fromTagBase(inbt, listType));
                }
                return objectList;
            case Constants.NBT.TAG_COMPOUND:
                return NBTTranslator.getViewFromCompound((RefNbtTagCompound) base);
            case Constants.NBT.TAG_INT_ARRAY:
                return ((IntArrayTag) base).getAsIntArray();
            case Constants.NBT.TAG_LONG_ARRAY:
                return ((LongArrayTag) base).getAsLongArray();
            default :
                return null;
        }
    }

    public void translateContainerToData(RefNbtTagCompound node, DataView container) {
        NBTTranslator.containerToCompound(container, node);
    }

    public DataContainer translateFrom(RefNbtTagCompound node) {
        return NBTTranslator.getViewFromCompound(node);
    }

    @Override
    public TypeToken<RefNbtTagCompound> token() {
        return NBTTranslator.TOKEN;
    }

    @Override
    public RefNbtTagCompound translate(DataView view) throws InvalidDataException {
        return NBTTranslator.containerToCompound(view);
    }

    @Override
    public DataContainer translate(RefNbtTagCompound obj) throws InvalidDataException {
        return NBTTranslator.getViewFromCompound(obj);
    }

    @Override
    public DataView addTo(RefNbtTagCompound compound, DataView container) {
        for (String key : compound.getAllKeys()) {
            Tag base = compound.get(key);
            byte type = base.getId();
            NBTTranslator.setInternal(base, type, container, key); // gotta love recursion
        }
        return container;
    }
}
