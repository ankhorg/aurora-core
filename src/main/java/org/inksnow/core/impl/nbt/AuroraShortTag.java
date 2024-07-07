package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagInt;
import org.inksnow.core.impl.ref.nbt.RefNbtTagShort;
import org.inksnow.core.nbt.IntTag;
import org.inksnow.core.nbt.ShortTag;

public final class AuroraShortTag extends AuroraNumericTag<AuroraShortTag, RefNbtTagShort> implements ShortTag {
    private static final boolean OF_SUPPORTED = CraftBukkitVersion.v1_15_R1.isSupport();
    private static final int HIGH = 1024;
    private static final int LOW = -128;
    private static final AuroraShortTag[] CACHE = buildInstanceCache();


    private final RefNbtTagShort ref;
    private final short value;

    /* package-private */ AuroraShortTag(RefNbtTagShort ref) {
        this.ref = ref;
        this.value = ref.asShort();
    }

    /* package-private */ AuroraShortTag(short value) {
        this.ref = OF_SUPPORTED ? RefNbtTagShort.of(value) : new RefNbtTagShort(value);
        this.value = value;
    }

    @Override
    public RefNbtTagShort impl$ref() {
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
        return (byte) value;
    }

    @Override
    public int getAsInt() {
        return value;
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
        if (o instanceof ShortTag) {
            return value == ((ShortTag) o).getAsShort();
        } else {
            return false;
        }
    }

    private static AuroraShortTag[] buildInstanceCache() {
        AuroraShortTag[] result = new AuroraShortTag[HIGH - LOW + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = new AuroraShortTag((short) (LOW + i));
        }
        return result;
    }

    /* package-private */ static AuroraShortTag wrap(RefNbtTagShort ref) {
        short value = ref.asShort();
        return (value >= LOW && value <= HIGH)
                ? CACHE[value - LOW]
                : new AuroraShortTag(ref);
    }

    public static AuroraShortTag of(short value) {
        return (value >= LOW && value <= HIGH)
                ? CACHE[value - LOW]
                : new AuroraShortTag(value);

    }
}
