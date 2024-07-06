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
public abstract class MutableAuroraCollectionValue<E, C extends Collection<E>,
        V extends CollectionValue.Mutable<E, C, V, I>, I extends CollectionValue.Immutable<E, C, I, V>>
        extends AbstractMutableAuroraValue<C> implements CollectionValue.Mutable<E, C, V, I> {

    public MutableAuroraCollectionValue(Key<? extends CollectionValue<E, C>> key, C element) {
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
        return this.element;
    }

    protected abstract V modifyCollection(Consumer<C> consumer);

    @Override
    public V add(E element) {
        return this.modifyCollection(collection -> collection.add(element));
    }

    @Override
    public V addAll(Iterable<E> elements) {
        return this.modifyCollection(collection -> Iterables.addAll(collection, elements));
    }

    @Override
    public V remove(E element) {
        if (!this.contains(element)) {
            return (V) this;
        }
        return this.modifyCollection(collection -> collection.remove(element));
    }

    @Override
    public V removeAll(Iterable<E> elements) {
        if (Streams.stream(elements).noneMatch(this::contains)) {
            return (V) this;
        }
        return this.modifyCollection(collection -> elements.forEach(collection::remove));
    }

    @Override
    public V removeAll(Predicate<E> predicate) {
        return this.modifyCollection(collection -> collection.removeIf(predicate));
    }

    @Override
    public V set(C value) {
        super.set(value);
        return (V) this;
    }

    @Override
    public V transform(Function<C, C> function) {
        return this.set(function.apply(this.get()));
    }

    @Override
    public Iterator<E> iterator() {
        return this.element.iterator();
    }
}
