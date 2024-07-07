package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagInt;
import org.inksnow.core.impl.ref.nbt.RefNbtTagLong;
import org.inksnow.core.nbt.IntTag;
import org.inksnow.core.nbt.LongTag;
import org.inksnow.core.nbt.Tag;

public final class AuroraLongTag extends AuroraNumericTag<AuroraLongTag, RefNbtTagLong> implements LongTag {
    private static final boolean OF_SUPPORTED = CraftBukkitVersion.v1_15_R1.isSupport();
    private static final int HIGH = 1024;
    private static final int LOW = -128;
    private static final AuroraLongTag[] CACHE = buildInstanceCache();


    private final RefNbtTagLong ref;
    private final long value;

    /* package-private */ AuroraLongTag(long value) {
        this.ref = OF_SUPPORTED ? RefNbtTagLong.of(value) : new RefNbtTagLong(value);
        this.value = value;
    }

    /* package-private */ AuroraLongTag(RefNbtTagLong ref) {
        this.ref = ref;
        this.value = ref.asLong();
    }

    @Override
    public RefNbtTagLong impl$ref() {
        return ref;
    }

    @Override
    public long getAsLong() {
        return value;
    }

    @Override
    public short getAsShort() {
        return (short) value;
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
        return (byte) value;
    }

    @Override
    public int getAsInt() {
        return (int) value;
    }

    @Override
    protected Number impl$asNumber() {
        return value;
    }

    @Override
    public Number getAsNumber() {
        return value;
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
        if (o instanceof LongTag) {
            return value == ((LongTag) o).getAsLong();
        } else {
            return false;
        }
    }

    private static AuroraLongTag[] buildInstanceCache() {
        AuroraLongTag[] result = new AuroraLongTag[HIGH - LOW + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = new AuroraLongTag(LOW + i);
        }
        return result;
    }

    public static AuroraLongTag of(long value) {
        return (value >= LOW && value <= HIGH)
                ? CACHE[(int) (value - LOW)]
                : new AuroraLongTag(value);
    }

    public static AuroraLongTag wrap(RefNbtTagLong ref) {
        long value = ref.asLong();
        return (value >= LOW && value <= HIGH)
                ? CACHE[(int) (value - LOW)]
                : new AuroraLongTag(ref);
    }

}
