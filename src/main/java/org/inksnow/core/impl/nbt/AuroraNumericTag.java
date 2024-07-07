package org.inksnow.core.impl.nbt;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtNumber;
import org.inksnow.core.nbt.NumericTag;
import org.inksnow.core.nbt.Tag;

public abstract class AuroraNumericTag<T extends AuroraNumericTag<T, R>, R extends RefNbtNumber> implements AuroraTag<R>, NumericTag {
    protected static final boolean AS_NUMBER_SUPPORTED = CraftBukkitVersion.v1_13_R1.isSupport();

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull T copy() {
        return (T) this;
    }

    @Override
    public long getAsLong() {
        return impl$ref().asLong();
    }

    @Override
    public short getAsShort() {
        return impl$ref().asShort();

    }

    @Override
    public double getAsDouble() {
        return impl$ref().asDouble();
    }

    @Override
    public float getAsFloat() {
        return impl$ref().asFloat();
    }

    @Override
    public byte getAsByte() {
        return impl$ref().asByte();
    }

    @Override
    public int getAsInt() {
        return impl$ref().asInt();
    }

    protected abstract Number impl$asNumber();

    @Override
    public Number getAsNumber() {
        if (AS_NUMBER_SUPPORTED) {
            return impl$ref().asNumber();
        } else {
            return impl$asNumber();
        }
    }
}
