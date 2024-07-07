package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagInt;
import org.inksnow.core.nbt.IntTag;
import org.inksnow.core.nbt.Tag;

public final class AuroraIntTag extends AuroraNumericTag<AuroraIntTag, RefNbtTagInt> implements IntTag {
    private static final boolean OF_SUPPORTED = CraftBukkitVersion.v1_15_R1.isSupport();
    private static final int HIGH = 1024;
    private static final int LOW = -128;
    private static final AuroraIntTag[] CACHE = buildInstanceCache();


    private final RefNbtTagInt ref;
    private final int value;

    /* package-private */ AuroraIntTag(int value) {
        this.ref = OF_SUPPORTED ? RefNbtTagInt.of(value) : new RefNbtTagInt(value);
        this.value = value;
    }

    /* package-private */ AuroraIntTag(RefNbtTagInt ref) {
        this.ref = ref;
        this.value = ref.asInt();
    }

    @Override
    public RefNbtTagInt impl$ref() {
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
        if (o instanceof IntTag) {
            return value == ((IntTag) o).getAsInt();
        } else {
            return false;
        }
    }

    private static AuroraIntTag[] buildInstanceCache() {
        AuroraIntTag[] result = new AuroraIntTag[HIGH - LOW + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = new AuroraIntTag(LOW + i);
        }
        return result;
    }

    public static AuroraIntTag of(int value) {
        return (value >= LOW && value <= HIGH)
                ? CACHE[value - LOW]
                : new AuroraIntTag(value);
    }

    public static AuroraIntTag wrap(RefNbtTagInt ref) {
        int value = ref.asInt();
        return (value >= LOW && value <= HIGH)
                ? CACHE[value - LOW]
                : new AuroraIntTag(ref);
    }
}
