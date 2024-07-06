package org.inksnow.core.impl.data.value;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.CollectionValue;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class ImmutableAuroraCollectionValue<E, C extends Collection<E>,
        I extends CollectionValue.Immutable<E, C, I, M>, M extends CollectionValue.Mutable<E, C, M, I>>
        extends AbstractImmutableAuroraValue<C> implements CollectionValue.Immutable<E, C, I, M> {

    public ImmutableAuroraCollectionValue(Key<? extends CollectionValue<E, C>> key, C element) {
        super(key, element);
    }

    @Override
    public int size() {
        return this.element.size();
    }

    @Override
    public boolean isEmpty() {
        return this.element.isEmpty();
    }

    @Override
    public boolean contains(E element) {
        return this.element.contains(element);
    }

    @Override
    public boolean containsAll(Iterable<E> iterable) {
        if (iterable instanceof Collection) {
            return this.element.containsAll((Collection<?>) iterable);
        }
        return Streams.stream(iterable).allMatch(this::contains);
    }

    @Override
    public C all() {
        return this.get();
    }

    protected abstract I modifyCollection(Consumer<C> consumer);

    @Override
    public I withElement(E element) {
        return this.modifyCollection(collection -> collection.add(element));
    }

    @Override
    public I withAll(Iterable<E> elements) {
        return this.modifyCollection(collection -> Iterables.addAll(collection, elements));
    }

    @Override
    public I without(E element) {
        if (!this.contains(element)) {
            return (I) this;
        }
        return this.modifyCollection(collection -> collection.remove(element));
    }

    @Override
    public I withoutAll(Iterable<E> elements) {
        if (Streams.stream(elements).noneMatch(this::contains)) {
            return (I) this;
        }
        return this.modifyCollection(collection -> elements.forEach(collection::remove));
    }

    @Override
    public I withoutAll(Predicate<E> predicate) {
        return this.modifyCollection(collection -> collection.removeIf(predicate));
    }

    @Override
    public I transform(Function<C, C> function) {
        return this.with(function.apply(this.get()));
    }

    @Override
    public Iterator<E> iterator() {
        return this.get().iterator();
    }
}
