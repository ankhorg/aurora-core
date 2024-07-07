package org.inksnow.core.impl.nbt;

import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagString;
import org.inksnow.core.nbt.StringTag;

public final class AuroraStringTag implements AuroraTag<RefNbtTagString>, StringTag {
    public static final boolean OF_SUPPORT = CraftBukkitVersion.v1_15_R1.isSupport();
    private static final AuroraStringTag EMPTY = new AuroraStringTag("");

    private final RefNbtTagString ref;
    private final String data;

    /* package-private */ AuroraStringTag(RefNbtTagString ref) {
        this.ref = ref;
        this.data = ref.asString();
    }

    /* package-private */ AuroraStringTag(String value) {
        this.ref = OF_SUPPORT ? RefNbtTagString.of(value) : new RefNbtTagString(value);
        this.data = value;
    }

    @Override
    public RefNbtTagString impl$ref() {
        return ref;
    }

    @Override
    public AuroraStringTag copy() {
        return this;
    }

    @Override
    public String getAsString() {
        return data;
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
        if (o instanceof StringTag) {
            return data.equals(((StringTag) o).getAsString());
        } else {
            return false;
        }
    }

    public static AuroraStringTag of(String value) {
        return value.isEmpty() ? EMPTY : new AuroraStringTag(value);
    }

    /* package-private */ static AuroraStringTag wrap(RefNbtTagString ref) {
        return ref.asString().isEmpty() ? EMPTY : new AuroraStringTag(ref);
    }
}
