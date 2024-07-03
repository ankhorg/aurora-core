package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.inksnow.core.spi.Testable;

@Slf4j
@UtilityClass
public class TestableUtil {
    public static boolean test(Object obj) {
        if (obj instanceof Testable) {
            try {
                return ((Testable) obj).isAvailable();
            } catch (Throwable e) {
                logger.error("Testable object {} is not available", obj.getClass(), e);
                return false;
            }
        } else {
            return true;
        }
    }
}
