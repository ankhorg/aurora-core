package org.inksnow.core.impl.nbt;

import org.inksnow.core.impl.ref.nbt.RefNbtBase;
import org.inksnow.core.nbt.CollectionTag;
import org.inksnow.core.nbt.Tag;

import java.util.AbstractList;

public abstract class AuroraCollectionTag<
        ER extends RefNbtBase,
        EE extends Tag
> extends AbstractList<EE> implements AuroraTag<RefNbtBase>, CollectionTag<EE> {
    @Override
    public abstract AuroraCollectionTag<ER, EE> copy();
}
