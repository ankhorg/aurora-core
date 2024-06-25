package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;
import org.inksnow.core.api.spi.Testable;

@UtilityClass
public class TestableUtil {
  public static boolean test(Object obj) {
    if (obj instanceof Testable) {
      try {
        return ((Testable) obj).isAvailable();
      } catch (Throwable e) {
        return false;
      }
    } else {
      return true;
    }
  }
}
