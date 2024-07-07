package org.inksnow.core.impl.data.key;

import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.event.ChangeDataHolderEvent;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.util.EventListener;

public final class KeyBasedDataListener<E extends DataHolder> implements EventListener<ChangeDataHolderEvent.ValueChange> {

    private final Class<E> holderType;
    private final Key<?> key;
    private final EventListener<ChangeDataHolderEvent.ValueChange> listener;

    KeyBasedDataListener(final Class<E> holderFilter, final Key<?> key, final EventListener<ChangeDataHolderEvent.ValueChange> listener) {
        this.holderType = holderFilter;
        this.key = key;
        this.listener = listener;
    }

    @Override
    public void handle(final ChangeDataHolderEvent.ValueChange event) throws Exception {
        if (this.holderType.isInstance(event.targetHolder()) && event.endResult().successfulData().stream().anyMatch(v -> v.key() == this.key)) {
            this.listener.handle(event);
        }
    }
}
