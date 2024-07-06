package org.inksnow.core.impl.ref.nbt;

import org.inksnow.ankhinvoke.comments.HandleBy;

@HandleBy(reference = "net/minecraft/nbt/NbtAccounter", predicates = "craftbukkit_version:[v1_20_R1,)")
public final class RefNbtAccounter {
    @HandleBy(reference = "Lnet/minecraft/nbt/NbtAccounter;<init>(JI)V", predicates = "craftbukkit_version:[v1_20_R1,)")
    public RefNbtAccounter(long maxBytes, int maxDepth) {
        throw new UnsupportedOperationException();
    }
}
