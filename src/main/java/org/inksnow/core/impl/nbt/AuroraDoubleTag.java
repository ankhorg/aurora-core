package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagDouble;
import org.inksnow.core.impl.ref.nbt.RefNbtTagFloat;
import org.inksnow.core.nbt.ByteTag;
import org.inksnow.core.nbt.DoubleTag;
import org.inksnow.core.nbt.FloatTag;

public final class AuroraDoubleTag extends AuroraNumericTag<AuroraDoubleTag, RefNbtTagDouble> implements DoubleTag {
    private static final boolean OF_SUPPORTED = CraftBukkitVersion.v1_15_R1.isSupport();

    private final RefNbtTagDouble ref;
    private final double value;

    /* package-private */ AuroraDoubleTag(double value) {
        this.ref = OF_SUPPORTED ? RefNbtTagDouble.of(value) : new RefNbtTagDouble(value);
        this.value = value;
    }

    /* package-private */ AuroraDoubleTag(RefNbtTagDouble ref) {
        this.ref = ref;
        this.value = ref.asDouble();
    }

    @Override
    public RefNbtTagDouble impl$ref() {
        return ref;
    }

    @Override
    public double getAsDouble() {
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
        if (o instanceof DoubleTag) {
            return value == ((DoubleTag) o).getAsDouble();
        } else {
            return false;
        }
    }

    public static AuroraDoubleTag of(float value) {
        return new AuroraDoubleTag(value);
    }
}
