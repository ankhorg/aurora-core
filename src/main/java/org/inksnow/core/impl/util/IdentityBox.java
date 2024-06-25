package org.inksnow.core.impl.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IdentityBox<T> {
  private final T value;

  public T get() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    IdentityBox<?> that = (IdentityBox<?>) obj;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
