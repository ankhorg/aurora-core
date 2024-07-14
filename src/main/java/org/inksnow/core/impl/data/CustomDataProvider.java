package org.inksnow.core.impl.data;

import io.leangen.geantyref.GenericTypeReflector;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.provider.MutableDataProvider;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

public class CustomDataProvider<V extends Value<E>, E> extends MutableDataProvider<V, E> {

    private final Set<Type> supportedTokens;

    public CustomDataProvider(final Key<V> key, final Set<Type> supportedTokens) {
        super(key);
        this.supportedTokens = supportedTokens;
    }

    @Override
    public Optional<E> get(DataHolder dataHolder) {
        /* if (this.isSupported(dataHolder)) {
            final SpongeDataHolderBridge customDataHolder = CustomDataProvider.getCustomDataHolder(dataHolder);
            return customDataHolder.bridge$get(this.key());
        } */
        return Optional.empty();
    }

    /* private static SpongeDataHolderBridge getCustomDataHolder(DataHolder dataHolder) {
        final SpongeDataHolderBridge customDataHolder;
        if (dataHolder instanceof ServerLocation) {
            customDataHolder = (SpongeDataHolderBridge) ((ServerLocation) dataHolder).blockEntity().get();
        } else {
            customDataHolder = (SpongeDataHolderBridge) dataHolder;
        }
        return customDataHolder;
    } */

    @Override
    public boolean isSupported(DataHolder dataHolder) {
        /* if (dataHolder instanceof ServerLocation) {
            if (!((ServerLocation) dataHolder).hasBlockEntity()) {
                return false;
            }
            for (final Type type : this.supportedTokens) {
                if (GenericTypeReflector.erase(type).isAssignableFrom(BlockEntity.class)) {
                    return true;
                }
            }
            return false;
        }
        if (!(dataHolder instanceof SpongeDataHolderBridge)) {
            return false;
        } */
        for (final Type type : this.supportedTokens) {
            if (GenericTypeReflector.erase(type).isAssignableFrom(dataHolder.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSupported(final Type dataHolder) {
        /* if (!SpongeDataHolderBridge.class.isAssignableFrom(GenericTypeReflector.erase(dataHolder))) {
            return true;
        } */
        for (final Type token : this.supportedTokens) {
            if (GenericTypeReflector.isSuperType(token, dataHolder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DataTransactionResult offer(DataHolder.Mutable dataHolder, E element) {
        /* if (this.isSupported(dataHolder)) {
            return CustomDataProvider.getCustomDataHolder(dataHolder).bridge$offer(this.key(),  element);
        } */
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult remove(DataHolder.Mutable dataHolder) {
        /* if (this.isSupported(dataHolder)) {
            return CustomDataProvider.getCustomDataHolder(dataHolder).bridge$remove(this.key());
        } */
        return DataTransactionResult.failNoData();
    }
}
