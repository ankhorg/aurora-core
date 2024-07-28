package org.inksnow.core.resource;

import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.common.value.qual.MatchesRegex;
import org.checkerframework.common.value.qual.MinLen;
import org.inksnow.core.Aurora;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.util.CopyableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a path to a resource.
 *
 * @implNote Use {@link #asString()} to get a string representation of the ResourcePath.
 * Implementations must override {@link #equals(Object)} and {@link #hashCode()}.
 * The equals method must compare the elements of the ResourcePath and return true if they are equal.
 * The hashCode method must return a hash code based on the elements of the ResourcePath.
 *
 * <pre>
 *     &#064;Override
 *     public boolean equals(Object obj) {
 *         if (obj == this) {
 *             return true;
 *         }
 *         if (!(obj instanceof ResourcePath)) {
 *             return false;
 *         }
 *         ResourcePath other = (ResourcePath) obj;
 *
 *         List&lt;String&gt; thisElements = this.elements();
 *         List&lt;String&gt; otherElements = other.elements();
 *
 *         if (thisElements.size() != otherElements.size()) {
 *             return false;
 *         }
 *         for (int i = 0; i < thisElements.size(); i++) {
 *             if (!thisElements.get(i).equals(otherElements.get(i))) {
 *                 return false;
 *             }
 *         }
 *         return true;
 *     }
 * </pre>
 *
 * <pre>
 *     &#064;Override
 *     public int hashCode() {
 *         List&lt;String&gt; elements = this.elements();
 *         int hashCode = 0;
 *         for (String element : elements) {
 *             hashCode = 31 * hashCode + element.hashCode();
 *         }
 *         return hashCode;
 *     }
 *  </pre>
 */
public interface ResourcePath extends Comparable<ResourcePath> {

    Supplier<Factory> FACTORY = Aurora.getFactoryLazy(Factory.class);

    /**
     * Returns a factory for creating ResourcePath instances.
     *
     * @return a factory for creating ResourcePath instances
     */
    static Factory factory() {
        return FACTORY.get();
    }

    /**
     * Creates a new ResourcePath builder.
     *
     * @return a new ResourcePath builder
     */
    static Builder builder() {
        return Aurora.createBuilder(Builder.class);
    }

    /**
     * Creates a ResourcePath with the specified elements.
     *
     * @param elements the elements of the ResourcePath
     * @return a ResourcePath with the specified elements
     * @throws IllegalArgumentException if the elements are invalid
     *
     * @implNote If element contains ':', it should be split into multiple elements.
     */
    static ResourcePath of(List<@NonEmpty String> elements) {
        return factory().of(elements);
    }

    /**
     * Creates a ResourcePath with the specified elements.
     *
     * @param elements the elements of the ResourcePath
     * @return a ResourcePath with the specified elements
     * @throws IllegalArgumentException if the elements are invalid
     *
     * @implNote If element contains ':', it should be split into multiple elements.
     */
    static ResourcePath of(@NonEmpty String ... elements) {
        return factory().of(elements);
    }

    /**
     * Parses the specified path and returns a ResourcePath.
     *
     * @param path the path to parse
     * @return a ResourcePath parsed from the specified path
     */
    static ResourcePath of(@NonEmpty String path){
            return factory().of(path);
    }

    /**
     * Returns the elements of the ResourcePath.
     *
     * @return the elements of the ResourcePath
     */
    List<@NonEmpty @MatchesRegex("[^:]+") String> elements();

    /**
     * Cast this ResourcePath to a DataQuery.
     *
     * @return a DataQuery with the same elements as this ResourcePath
     */
    default DataQuery asDataQuery() {
        return DataQuery.of(elements());
    }

    /**
     * Returns the parent of the ResourcePath, include itself.
     * <p>
     * For example, if the ResourcePath is "a:b:c", the parent is ["a:b:c", "a:b", "a"]
     *
     * @return the parent of the ResourcePath, include itself
     */
    default @NonEmpty List<ResourcePath> allSubPaths() {
        final List<String> elements = elements();
        final int size = elements.size();
        final List<ResourcePath> subPaths = new ArrayList<>(size);
        subPaths.add(this);
        for (int i = size - 1; i >= 0; i--) {
            subPaths.add(ResourcePath.of(elements.subList(i, size)));
        }
        return subPaths;
    }

    /**
     * Compares this ResourcePath with the specified ResourcePath for order.
     * Returns a negative integer, zero, or a positive integer as this ResourcePath is less than, equal to, or greater
     * than the specified ResourcePath.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this ResourcePath is less than, equal to, or greater
     */
    @Override
    default int compareTo(ResourcePath other) {
        final List<String> thisElements = this.elements();
        final List<String> otherElements = other.elements();

        final int size = Math.min(thisElements.size(), otherElements.size());
        for (int i = 0; i < size; i++) {
            final int cmp = thisElements.get(i).compareTo(otherElements.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(thisElements.size(), otherElements.size());
    }

    /**
     * Returns a string representation of the ResourcePath.
     * The string representation consists of the elements separated by ":".
     *
     * @return a string representation of the ResourcePath
     */
    default String asString() {
        return String.join(":", elements());
    }

    /**
     * Returns a builder of this ResourcePath.
     *
     * @return a builder of this ResourcePath
     */
    default Builder toBuilder() {
        return Aurora.createBuilder(Builder.class).copy(this);
    }

    /**
     * A builder for {@link ResourcePath}.
     *
     * @implNote If elements has some invalid elements, you still need to add elements before throwing an exception.
     */
    interface Builder extends CopyableBuilder<ResourcePath, Builder> {
        /**
         * Adds an element to the {@link ResourcePath}.
         *
         * @param element the element to add
         * @return this builder
         * @throws IllegalArgumentException if the element is invalid
         */
        @This Builder add(@NonEmpty String element) throws IllegalArgumentException;

        /**
         * Adds elements to the {@link ResourcePath}.
         *
         * @param elements the elements to add
         * @return this builder
         * @throws IllegalArgumentException if the elements are invalid
         */
        default @This Builder add(@NonEmpty String ... elements) throws IllegalArgumentException {
            for (String element : elements) {
                add(element);
            }
            return getThis();
        }

        /**
         * Adds elements to the {@link ResourcePath}.
         *
         * @param elements the elements to add
         * @return this builder
         * @throws IllegalArgumentException if the elements are invalid
         */
        default @This Builder add(List<@NonEmpty String> elements) throws IllegalArgumentException {
            for (String element : elements) {
                add(element);
            }
            return getThis();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default @This Builder copy(ResourcePath value) {
            reset();
            return add(value.elements());
        }
    }

    /**
     * A factory for {@link ResourcePath}.
     */
    interface Factory {
        /**
         * Creates a {@link ResourcePath} with the specified elements.
         *
         * @param elements the elements of the {@link ResourcePath}
         * @return a {@link ResourcePath} with the specified elements
         * @throws IllegalArgumentException if the elements are invalid
         *
         * @implNote If element contains ':', it should be split into multiple elements.
         */
        ResourcePath of(List<@NonEmpty String> elements) throws IllegalArgumentException;

        /**
         * Creates a {@link ResourcePath} with the specified elements.
         *
         * @param elements the elements of the {@link ResourcePath}
         * @return a {@link ResourcePath} with the specified elements
         * @throws IllegalArgumentException if the elements are invalid
         *
         * @implNote If element contains ':', it should be split into multiple elements.
         */
        ResourcePath of(@NonEmpty String ... elements);

        /**
         * Parses the specified path and returns a {@link ResourcePath}.
         * @param path the path to parse
         * @return a {@link ResourcePath} parsed from the specified path
         */
        ResourcePath of(@NonEmpty String path);
    }
}
