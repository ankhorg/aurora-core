package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class NamespaceUtil {
    public static List<String> parseNameSpace(String namespace) {
        final List<String> result = new ArrayList<>();
        int split = namespace.length();
        while ((split = namespace.lastIndexOf(':', split - 1)) != -1) {
            result.add(namespace.substring(split + 1));
        }
        result.add(namespace);
        return result;
    }
}
