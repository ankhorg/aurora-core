package org.inksnow.core.impl.data.provider;

import io.leangen.geantyref.GenericTypeReflector;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.TypeTokenUtil;

import java.lang.reflect.Type;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class GenericImmutableDataProviderBase<H, V extends Value<E>, E> extends ImmutableDataProvider<V, E> implements
        AbstractDataProvider.KnownHolderType {

    private final Class<H> holderType;

    GenericImmutableDataProviderBase(final Key<V> key, final Class<H> holderType) {
        super(key);
        this.holderType = holderType;
    }

    GenericImmutableDataProviderBase(final Key<V> key) {
        super(key);
        this.holderType = (Class<H>) GenericTypeReflector.erase(
                TypeTokenUtil.typeArgumentFromSupertype(this.getClass(), GenericImmutableDataProviderBase.class, 0));
    }

    private boolean isTypeAllowed(final DataHolder dataHolder) {
        return this.holderType.isInstance(dataHolder);
    }

    @Override
    public Class<H> getHolderType() {
        return this.holderType;
    }

    /**
     * Gets whether the target data holder is supported.
     *
     * @param dataHolder The data holder
     * @return Whether supported
     */
    protected boolean supports(final H dataHolder) {
        return true;
    }

    /**
     * Attempts to get data from the target data holder.
     *
     * @param dataHolder The data holder
     * @return The element, if present
     */
    protected abstract Optional<E> getFrom(H dataHolder);

    /**
     * Attempts to set data for the target data holder.
     *
     * @param dataHolder The data holder
     * @param value The value
     * @return The new immutable object, if successful
     */
    protected abstract Optional<H> set(H dataHolder, E value);

    /**
     * Constructs a value for the given element and data holder.
     *
     * @param dataHolder The data holder
     * @param element The element
     * @return The value
     */
    protected V constructValue(final H dataHolder, final E element) {
        return Value.genericImmutableOf(this.key(), element);
    }

    /**
     * Attempts to remove the data from the target data holder.
     *
     * @param dataHolder The data holder
     * @return The new immutable object, if successful
     */
    protected Optional<H> removeFrom(final H dataHolder) {
        return Optional.empty();
    }

    @Override
    public final Optional<E> get(final DataHolder dataHolder) {
        if (!this.isSupported(dataHolder)) {
            return Optional.empty();
        }
        return this.getFrom((H) dataHolder);
    }

    @Override
    public Optional<V> value(final DataHolder dataHolder) {
        return this.get(dataHolder).map(e -> this.constructValue((H) dataHolder, e));
    }

    @Override
    public boolean isSupported(final DataHolder dataHolder) {
        return this.isTypeAllowed(dataHolder) && this.supports((H) dataHolder);
    }

    @Override
    public boolean isSupported(final Type dataHolder) {
        return this.holderType.isAssignableFrom(GenericTypeReflector.erase(dataHolder));
    }

    @Override
    public <I extends DataHolder.Immutable<I>> Optional<I> with(final I immutable, final E value) {
        if (!this.isSupported(immutable)) {
            return Optional.empty();
        }
        return (Optional<I>) this.set((H) immutable, value);
    }

    @Override
    public <I extends DataHolder.Immutable<I>> Optional<I> without(final I immutable) {
        if (!this.isSupported(immutable)) {
            return Optional.empty();
        }
        return (Optional<I>) this.removeFrom((H) immutable);
    }
}
