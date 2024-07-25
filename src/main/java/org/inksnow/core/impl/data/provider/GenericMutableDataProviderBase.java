package org.inksnow.core.impl.data.provider;

import io.leangen.geantyref.GenericTypeReflector;
import lombok.extern.slf4j.Slf4j;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.TypeTokenUtil;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
@Slf4j
public abstract class GenericMutableDataProviderBase<H, V extends Value<E>, E> extends MutableDataProvider<V, E>
        implements AbstractDataProvider.KnownHolderType {

    private final Class<H> holderType;

    protected GenericMutableDataProviderBase(final Supplier<? extends Key<V>> key, final Class<H> holderType) {
        this(key.get(), holderType);
    }

    protected GenericMutableDataProviderBase(final Key<V> key, final Class<H> holderType) {
        super(key);
        this.holderType = holderType;
    }

    protected GenericMutableDataProviderBase(final Supplier<? extends Key<V>> key) {
        this(key.get());
    }

    protected GenericMutableDataProviderBase(final Key<V> key) {
        super(key);
        this.holderType = (Class<H>) GenericTypeReflector.erase(
                TypeTokenUtil.typeArgumentFromSupertype(this.getClass(), GenericMutableDataProviderBase.class, 0));
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
     * Attempts to get data as a value from the target data holder
     *
     * @param dataHolder The data holder
     * @return The value, if present
     */
    public Optional<V> getValueFrom(final H dataHolder) {
        return this.getFrom(dataHolder).map(e -> this.constructValue(dataHolder, e));
    }

    /**
     * Attempts to set data for the target data holder.
     *
     * @param dataHolder The data holder
     * @param value The element
     * @return Whether applying was successful
     */
    protected boolean set(final H dataHolder, final E value) {
        return false;
    }

    /**
     * Attempts to offer data to the target data holder.
     *
     * @param dataHolder The data holder
     * @param value The element
     * @return Whether applying was successful
     */
    protected DataTransactionResult setAndGetResult(final H dataHolder, final E value) {
        final Optional<Value.Immutable<E>> originalValue = this.getFrom(dataHolder)
                .map(e -> this.constructValue(dataHolder, e).asImmutable());
        final Value.Immutable<E> originalReplacementValue = Value.immutableOf(this.key(), value);

        return this.callReplacementEvent((DataHolder.Mutable) dataHolder, originalValue, originalReplacementValue).map(replacementValue -> {
            try {
                if (this.set(dataHolder, value)) {
                    final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                    originalValue.ifPresent(builder::replace);
                    return builder.result(DataTransactionResult.Type.SUCCESS).success(replacementValue).build();
                }
                return DataTransactionResult.failResult(replacementValue);
            } catch (Exception e) {
                logger.debug("An exception occurred when setting data: ", e);
                return DataTransactionResult.errorResult(replacementValue);
            }
        }).orElse(DataTransactionResult.failResult(originalReplacementValue));
    }

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
     * @return Whether the removal was successful
     */
    protected boolean delete(H dataHolder) {
        return false;
    }

    /**
     * Attempts to remove the data from the target data holder.
     *
     * @param dataHolder The data holder
     * @return Whether the removal was successful
     */
    protected DataTransactionResult deleteAndGetResult(final H dataHolder) {
        final Optional<Value.Immutable<E>> originalValue = this.getFrom(dataHolder)
                .map(e -> this.constructValue(dataHolder, e).asImmutable());
        if (!originalValue.isPresent()) {
            return DataTransactionResult.failNoData();
        }
        if (this.delete(dataHolder)) {
            return DataTransactionResult.successRemove(originalValue.get());
        }
        return DataTransactionResult.failNoData();
    }

    @Override
    public final boolean isSupported(final DataHolder dataHolder) {
        return this.isTypeAllowed(dataHolder) && this.supports((H) dataHolder);
    }

    @Override
    public boolean isSupported(final Type dataHolder) {
        return this.holderType.isAssignableFrom(GenericTypeReflector.erase(dataHolder));
    }

    @Override
    public final Optional<V> value(final DataHolder dataHolder) {
        if (!this.isSupported(dataHolder)) {
            return Optional.empty();
        }
        return this.getValueFrom((H) dataHolder);
    }

    @Override
    public final Optional<E> get(final DataHolder dataHolder) {
        if (!this.isSupported(dataHolder)) {
            return Optional.empty();
        }
        return this.getFrom((H) dataHolder);
    }

    @Override
    public final DataTransactionResult offerValue(final DataHolder.Mutable dataHolder, final V value) {
        if (!this.isSupported(dataHolder)) {
            return DataTransactionResult.failNoData();
        }

        final Optional<Value.Immutable<E>> originalValue = this.getFrom((H) dataHolder)
                .map(e -> this.constructValue((H) dataHolder, e).asImmutable());
        final Value.Immutable<E> originalReplacementValue = value.asImmutable();

        return this.callReplacementEvent(dataHolder, originalValue, originalReplacementValue).map(replacementValue -> {
            try {
                if (this.set((H) dataHolder, value.get())) {
                    final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                    originalValue.ifPresent(builder::replace);
                    return builder.result(DataTransactionResult.Type.SUCCESS).success(replacementValue).build();
                }
                return DataTransactionResult.failResult(replacementValue);
            } catch (Exception e) {
                logger.debug("An exception occurred when setting data: ", e);
                return DataTransactionResult.errorResult(replacementValue);
            }
        }).orElse(DataTransactionResult.failResult(originalReplacementValue));
    }

    @Override
    public final DataTransactionResult offer(final DataHolder.Mutable dataHolder, final E element) {
        if (!this.isSupported(dataHolder)) {
            return DataTransactionResult.failResult(Value.immutableOf(this.key(), element));
        }
        return this.setAndGetResult((H) dataHolder, element);
    }

    @Override
    public final DataTransactionResult remove(final DataHolder.Mutable dataHolder) {
        if (!this.isSupported(dataHolder)) {
            return DataTransactionResult.failNoData();
        }
        return this.deleteAndGetResult((H) dataHolder);
    }

    private Optional<Value.Immutable<E>> callReplacementEvent(
            final DataHolder.Mutable dataHolder, final Optional<Value.Immutable<E>> originalValue, final Value.Immutable<E> replacementValue) {
        /* if (!(dataHolder instanceof Entity entity) || ((EntityAccessor) entity).accessor$levelCallback() == EntityInLevelCallback.NULL
                || ((SpongeDataHolderBridge) dataHolder).brigde$isDeserializing()) {
            return Optional.of(replacementValue);
        }
        final DataTransactionResult.Builder transaction = DataTransactionResult.builder()
                .success(replacementValue)
                .result(DataTransactionResult.Type.SUCCESS);
        originalValue.ifPresent(transaction::replace);
        final ChangeDataHolderEvent.ValueChange valueChange = SpongeEventFactory.createChangeDataHolderEventValueChange(
                PhaseTracker.SERVER.currentCause(),
                transaction.build(),
                dataHolder);
        if (SpongeCommon.post(valueChange)) {
            return Optional.empty();
        }
        return valueChange.endResult().successfulValue(replacementValue.key());
         */
        logger.warn("callReplacementEvent is not implemented, dataHolder: {}, originalValue: {}, replacementValue: {}", dataHolder, originalValue, replacementValue);
        return Optional.of(replacementValue);
    }
}
