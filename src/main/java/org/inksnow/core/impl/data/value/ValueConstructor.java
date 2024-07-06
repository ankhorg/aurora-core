package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

public interface ValueConstructor<V extends Value<E>, E> {

    V getMutable(E element);

    default V getImmutable(E element) {
        return this.getRawImmutable(CopyHelper.copy(element));
    }

    V getRawImmutable(E element);
}
