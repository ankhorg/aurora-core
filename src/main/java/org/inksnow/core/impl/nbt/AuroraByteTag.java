package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagByte;
import org.inksnow.core.nbt.ByteArrayTag;
import org.inksnow.core.nbt.ByteTag;

import java.util.Arrays;

public final class AuroraByteTag extends AuroraNumericTag<AuroraByteTag, RefNbtTagByte> implements ByteTag {
    private static final boolean OF_SUPPORTED = CraftBukkitVersion.v1_15_R1.isSupport();
    private static final AuroraByteTag[] CACHE = buildInstanceCache();
    private static final AuroraByteTag FALSE = of((byte) 0);
    private static final AuroraByteTag TRUE = of((byte) 1);

    private final RefNbtTagByte ref;
    private final byte value;
    private final Byte valueBoxed;

    /* package-private */ AuroraByteTag(byte value) {
        this.ref = OF_SUPPORTED ? RefNbtTagByte.of(value) : new RefNbtTagByte(value);
        this.value = value;
        this.valueBoxed = value;
    }

    @Override
    public RefNbtTagByte impl$ref() {
        return ref;
    }

    @Override
    public long getAsLong() {
        return value;
    }

    @Override
    public short getAsShort() {
        return value;
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public float getAsFloat() {
        return value;
    }

    @Override
    public byte getAsByte() {
        return value;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    protected Number impl$asNumber() {
        return valueBoxed;
    }

    @Override
    public Number getAsNumber() {
        return valueBoxed;
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
        if (o instanceof ByteTag) {
            return value == ((ByteTag) o).getAsByte();
        } else {
            return false;
        }
    }

    private static AuroraByteTag[] buildInstanceCache() {
        AuroraByteTag[] result = new AuroraByteTag[256];
        for (int i = 0; i < result.length; i++) {
            result[i] = new AuroraByteTag((byte) (i - 128));
        }
        return result;
    }

    public static AuroraByteTag of(byte value) {
        return CACHE[value + 128];
    }

    public static AuroraByteTag of(boolean value) {
        return value ? TRUE : FALSE;
    }
}
