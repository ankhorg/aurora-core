package org.inksnow.core.impl.resource;

import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.common.value.qual.MatchesRegex;
import org.inksnow.core.resource.ResourcePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public final class AuroraResourcePathBuilder implements ResourcePath.Builder {
    private final List<@NonEmpty @MatchesRegex("[^:]+") String> elements = new ArrayList<>();

    @Override
    public AuroraResourcePathBuilder add(@NonEmpty String element) throws IllegalArgumentException {
        if (element.contains(":")) {
            elements.addAll(Arrays.asList(StringUtils.split(element, ':')));
        } else {
            elements.add(element);
        }
        return this;
    }

    @Override
    public AuroraResourcePathBuilder reset() {
        elements.clear();
        return this;
    }

    @Override
    public AuroraResourcePath build() {
        if (elements.size() < 2) {
            throw new IllegalArgumentException("ResourcePath must have at least 2 elements");
        }
        return new AuroraResourcePath(elements);
    }

    @Override
    public String toString() {
        return "AuroraResourcePathBuilder" + this.elements;
    }
}
