package org.inksnow.core.impl.resource;

import lombok.Getter;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.MatchesRegex;
import org.inksnow.core.resource.ResourcePath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AuroraResourcePath implements ResourcePath {
    @Getter
    private final List<@NonEmpty @MatchesRegex("[^:]+") String> elements;
    private final int hashCode;
    private final String asString;

    /* package-private */ AuroraResourcePath(List<@NonEmpty @MatchesRegex("[^:]+") String> elements) {
        this.elements = Collections.unmodifiableList(new ArrayList<>(elements));

        this.hashCode = hashCodeImpl(elements);
        this.asString = String.join(":", elements);
    }

    private static int hashCodeImpl(List<String> elements) {
        int hashCode = 0;
        for (String element : elements) {
            hashCode = 31 * hashCode + element.hashCode();
        }
        return hashCode;
    }

    @Override
    public String asString() {
        return asString;
    }

    @Override
    public String toString() {
        return asString;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ResourcePath)) {
            return false;
        }
        final ResourcePath other = (ResourcePath) obj;

        final List<String> thisElements = this.elements();
        final List<String> otherElements = other.elements();

        if (thisElements.size() != otherElements.size()) {
            return false;
        }
        for (int i = 0; i < thisElements.size(); i++) {
            if (!thisElements.get(i).equals(otherElements.get(i))) {
                return false;
            }
        }
        return true;
    }
}
