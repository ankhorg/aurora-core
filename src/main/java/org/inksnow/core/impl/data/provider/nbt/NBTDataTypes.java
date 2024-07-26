package org.inksnow.core.impl.data.provider.nbt;

public final class NBTDataTypes {

    public static final NBTDataType ENTITY = new NBTDataType("entity");
    public static final NBTDataType BLOCK = new NBTDataType("block");
    public static final NBTDataType CHUNK = new NBTDataType("chunk");
    public static final NBTDataType WORLD = new NBTDataType("world");
    public static final NBTDataType ITEMSTACK = new NBTDataType("itemstack");
    public static final NBTDataType PLAYER = new NBTDataType("player");

    private NBTDataTypes() {
    }
}
