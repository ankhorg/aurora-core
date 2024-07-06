package org.inksnow.core.impl.resource;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.dataflow.qual.Pure;
import org.inksnow.core.resource.ResourcePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public final class AuroraResourcePathFactory implements ResourcePath.Factory {
    @Pure
    @Override
    public ResourcePath of(List<@NonEmpty String> elements) {
        boolean noSplit = true;
        for (String element : elements) {
            if (element.contains(":")) {
                noSplit = false;
                break;
            }
        }
        // If all element does not contain split, fast return
        if (noSplit) {
            return new AuroraResourcePath(elements);
        }

        final List<String> newElements = new ArrayList<>();
        for (String element : elements) {
            if (element.contains(":")) {
                newElements.addAll(Arrays.asList(StringUtils.split(element, ':')));
            } else {
                newElements.add(element);
            }
        }
        return new AuroraResourcePath(newElements);
    }

    @Override
    public ResourcePath of(@NonEmpty String... elements) {
        return of(Arrays.asList(elements));
    }

    @Override
    public ResourcePath of(@NonEmpty String path) {
        return new AuroraResourcePath(Arrays.asList(StringUtils.split(path, ':')));
    }
}
