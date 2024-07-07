package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagByte;
import org.inksnow.core.impl.ref.nbt.RefNbtTagFloat;
import org.inksnow.core.nbt.ByteTag;
import org.inksnow.core.nbt.FloatTag;

public final class AuroraFloatTag extends AuroraNumericTag<AuroraFloatTag, RefNbtTagFloat> implements FloatTag {
    private static final boolean OF_SUPPORTED = CraftBukkitVersion.v1_15_R1.isSupport();

    private final RefNbtTagFloat ref;
    private final float value;

    /* package-private */ AuroraFloatTag(float value) {
        this.ref = OF_SUPPORTED ? RefNbtTagFloat.of(value) : new RefNbtTagFloat(value);
        this.value = value;
    }

    /* package-private */ AuroraFloatTag(RefNbtTagFloat ref) {
        this.ref = ref;
        this.value = ref.asFloat();
    }

    @Override
    public RefNbtTagFloat impl$ref() {
        return ref;
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
        if (o instanceof FloatTag) {
            return value == ((FloatTag) o).getAsFloat();
        } else {
            return false;
        }
    }

    public static AuroraFloatTag of(float value) {
        return new AuroraFloatTag(value);
    }
}
