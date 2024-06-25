package org.inksnow.core.impl.spi;

import lombok.Getter;
import lombok.NonNull;
import org.inksnow.core.api.spi.Namespaced;
import org.inksnow.core.api.spi.SpiRegistry;
import org.inksnow.core.impl.util.NamespaceUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractSpiRegistry<S extends Namespaced> implements SpiRegistry<S> {
  private final AuroraServiceLoader<S> loader;
  private final @NonNull Map<@NonNull String, S> services;

  public AbstractSpiRegistry(Class<S> serviceClass) {
    this.loader = new AuroraServiceLoader<>(serviceClass);
    this.services = new ConcurrentHashMap<>();
  }

  public abstract String friendlyName();

  @Override
  public S get(@NonNull String namespace) {
    return services.get(namespace);
  }

  @Override
  public void register(@NonNull S service) {
    List<String> namespaces = NamespaceUtil.parseNameSpace(service.namespace());
    for (String namespace : namespaces) {
      services.putIfAbsent(namespace, service);
    }
  }

  @Override
  public void registerForce(@NonNull S service) {
    List<String> namespaces = NamespaceUtil.parseNameSpace(service.namespace());
    for (String namespace : namespaces) {
      services.put(namespace, service);
    }
  }
}
