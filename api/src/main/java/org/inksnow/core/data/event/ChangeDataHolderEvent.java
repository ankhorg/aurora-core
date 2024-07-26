package org.inksnow.core.data.event;

import org.bukkit.event.Cancellable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.value.Value;

/**
 * An event that is associated with a {@link DataHolder.Mutable} that may have some
 * {@link Value}s changed, offered, or removed. Note that calling any
 * methods relating to modifying a {@link DataHolder.Mutable} while this event
 * is being processed may produce awkward results.
 */
public interface ChangeDataHolderEvent extends Cancellable {

    /**
     * Gets the {@link DataHolder.Mutable} targeted in this event.
     *
     * @return The data holder targeted in this event
     */
    DataHolder.Mutable targetHolder();

    interface ValueChange extends ChangeDataHolderEvent {

        /**
         * Gets the original {@link DataTransactionResult} of the {@link Value.Mutable}s
         * that have changed in this event.
         *
         * @return The original changes of values
         */
        DataTransactionResult originalChanges();

        /**
         * Submits a new {@link DataTransactionResult} as a proposal of various
         * {@link Value.Mutable}s to be successfully offered/changed on the original
         * {@link DataHolder.Mutable}.
         *
         * <p>If the proposed {@link DataTransactionResult} provides additional
         * values that were not changed in the {@link #originalChanges()},
         * the provided changes suggested to be successfully offered will be
         * re-offered </p>
         *
         * @param result The resulting offer
         * @return This event, for chaining
         */
        ValueChange proposeChanges(DataTransactionResult result);

        /**
         * Gets the ending resulting {@link DataTransactionResult} that will be
         * offered to the {@link DataHolder}.
         *
         * @return The final transaction details to be submitted
         */
        DataTransactionResult endResult();
    }
}
