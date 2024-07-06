package org.inksnow.core.impl.data.holder;

import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

@SuppressWarnings("type.argument")
public final class SimpleNBTDataHolder implements AuroraMutableDataHolder {

    private final byte nbtDataType;
    private final RefNbtTagCompound compound;

    public SimpleNBTDataHolder(final RefNbtTagCompound compound) {
        this.compound = compound;
        this.nbtDataType = compound.getTypeId();
    }
}
