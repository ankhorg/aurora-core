package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.value.Value;

final class CachedBooleanValueConstructor implements ValueConstructor<Value<Boolean>, Boolean> {

    private final ValueConstructor<Value<Boolean>, Boolean> original;
    private final Value<Boolean> immutableValueTrue;
    private final Value<Boolean> immutableValueFalse;

    CachedBooleanValueConstructor(final ValueConstructor<Value<Boolean>, Boolean> original) {
        this.original = original;
        this.immutableValueFalse = original.getImmutable(false);
        this.immutableValueTrue = original.getImmutable(true);
    }

    @Override
    public Value<Boolean> getMutable(final Boolean element) {
        return this.original.getMutable(element);
    }

    @Override
    public Value<Boolean> getImmutable(final Boolean element) {
        return element ? this.immutableValueTrue : this.immutableValueFalse;
    }

    @Override
    public Value<Boolean> getRawImmutable(final Boolean element) {
        return this.getImmutable(element);
    }
}
