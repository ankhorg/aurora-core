package org.inksnow.core.util;

public interface Copyable<T extends Copyable<T>> {
    T copy();
}
