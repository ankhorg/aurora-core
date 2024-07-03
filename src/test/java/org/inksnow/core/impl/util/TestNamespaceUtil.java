package org.inksnow.core.impl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestNamespaceUtil {
    @Test
    public void testParseNameSpace() {
        Assertions.assertEquals(Arrays.asList("c", "b:c", "a:b:c"), NamespaceUtil.parseNameSpace("a:b:c"));
        Assertions.assertEquals(Arrays.asList("b", "a:b"), NamespaceUtil.parseNameSpace("a:b"));
    }
}
