package org.inksnow.core.data.holder;

import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.util.Builder;
import org.inksnow.core.util.CopyableBuilder;

import java.util.function.Supplier;

public interface DataHolderBuilder<H extends DataHolder, B extends DataHolderBuilder<H, B>> extends Builder<H, B>, CopyableBuilder<H, B> {

    /**
     * Adds the given {@link Value} to the builder. The
     * {@link Value} is copied when the {@link DataHolder}
     * is created.
     *
     * @param value The value to add
     * @return This builder, for chaining
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default B add(Value<?> value) {
        return (B) this.add((Key) value.key(), value.get());
    }

    /**
     * Adds all the {@link Value}s to the builder. The
     * {@link Value}s are copied when the {@link DataHolder}
     * is created.
     *
     * @param values The values to add
     * @return This builder, for chaining
     */
    @SuppressWarnings("unchecked")
    default B add(Iterable<? extends Value<?>> values) {
        values.forEach(this::add);
        return (B) this;
    }

    /**
     * Adds all the {@link Value}s from the {@link DataManipulator}
     * to the builder. The {@link Value}s are copied when the
     * {@link DataHolder} is created.
     *
     * @param manipulator The manipulator to add
     * @return This builder, for chaining
     */
    default B add(DataManipulator manipulator) {
        return this.add(manipulator.getValues());
    }

    /**
     * Adds all the {@link Value}s from the {@link DataHolder}
     * to the builder. The {@link Value}s are copied when the
     * {@link DataHolder} is created.
     *
     * @param dataHolder The data holder to add data from
     * @return This builder, for chaining
     */
    default B addFrom(DataHolder dataHolder) {
        return this.add(dataHolder.getValues());
    }

    /**
     * Adds the given {@link Key} with the given value.
     *
     * @param key The key to assign the value with
     * @param value The value to assign with the key
     * @param <V> The type of the value
     * @return This builder, for chaining
     */
    <V> B add(Key<? extends Value<V>> key, V value);

    /**
     * Adds the given {@link Key} with the given value.
     *
     * @param key The key to assign the value with
     * @param value The value to assign with the key
     * @param <V> The type of the value
     * @return This builder, for chaining
     */
    default <V> B add(Supplier<? extends Key<? extends Value<V>>> key, V value) {
        return this.add(key.get(), value);
    }

    /**
     * Copies all known {@link DataManipulator}s from the given
     * {@link DataHolder} of type {@link H}. This is a
     * defensive copy as {@link DataManipulator} is mutable.
     *
     * @param holder The {@link DataHolder} to copy from
     * @return This builder for chaining
     */
    @Override
    B copy(H holder);

    @Override
    B reset();

    interface Mutable<H extends DataHolder.Mutable, B extends Mutable<H, B>> extends DataHolderBuilder<H, B> {

    }

    interface Immutable<H extends DataHolder.Immutable<H>, B extends Immutable<H, B>> extends DataHolderBuilder<H, B> {

    }
}
