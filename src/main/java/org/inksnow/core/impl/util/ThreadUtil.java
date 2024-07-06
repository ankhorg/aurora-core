package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class ThreadUtil {
    public static void ensureMainThread() throws IllegalStateException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called on the main thread");
        }
    }
}
