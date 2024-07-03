package org.inksnow.core.impl.spi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.AuroraCore;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.resource.WithResourcePath;
import org.inksnow.core.spi.ServicePriority;
import org.inksnow.core.spi.SpiRegistry;
import org.inksnow.core.spi.WithServicePriority;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractSpiRegistry<S extends WithResourcePath> implements SpiRegistry<S> {
    private static final Comparator<WithResourcePath> SERVICE_COMPARATOR =
        Comparator.<WithResourcePath, ServicePriority>comparing(AbstractSpiRegistry::getPriority)
            .thenComparing(WithResourcePath::resourcePath)
            .thenComparing(it -> {
                throw new IllegalStateException("Unexpected");
            });

    @Getter
    private final AuroraServiceLoader<S> loader;
    private final Map<ResourcePath, S> services;
    private final Map<ResourcePath, ServiceSelector> selectors;

    public AbstractSpiRegistry(Class<S> serviceClass) {
        this.loader = new AuroraServiceLoader<>(serviceClass);
        this.services = new ConcurrentHashMap<>();
        this.selectors = new ConcurrentHashMap<>();
    }

    public abstract String friendlyName();

    @Override
    public @Nullable S get(ResourcePath resourcePath) {
        return services.get(resourcePath);
    }

    @Override
    public void register(S service) {
        final ResourcePath resourcePath = service.resourcePath();
        if (services.put(resourcePath, service) != null) {
            logger.warn("{} 重复注册: '{}'", friendlyName(), resourcePath);
        }

        for (ResourcePath subPath : resourcePath.allSubPaths()) {
            selectors.computeIfAbsent(subPath, ServiceSelector::new).add(service);
        }
    }

    private static ServicePriority getPriority(WithResourcePath service) {
        return service instanceof WithServicePriority
            ? ((WithServicePriority) service).priority()
            : ServicePriority.NORMAL;
    }

    @RequiredArgsConstructor
    private final class ServiceSelector {
        private final ResourcePath path;
        private final Map<ResourcePath, S> services = new HashMap<>();
        private volatile @MonotonicNonNull S selected;

        private synchronized boolean add(S service) {
            if (selected != null) {
                logger.warn("已经选择了服务，注册将会被忽略: {}", selected.resourcePath());
                return false;
            }
            return services.put(service.resourcePath(), service) != null;
        }

        private S get() {
            @Nullable S selected = this.selected;
            if (selected != null) {
                return selected;
            }
            synchronized (this) {
                selected = this.selected;
                if (selected != null) {
                    return selected;
                }
                selected = selectImpl();
                this.selected = selected;
            }
            return selected;
        }

        private synchronized S selectImpl() {
            if (services.isEmpty()) {
                throw new IllegalStateException("No service available");
            }
            if (services.size() == 1) {
                return services.values().iterator().next();
            }

            final List<S> sortedServices = services.values()
                .stream()
                .sorted(SERVICE_COMPARATOR)
                .collect(Collectors.toList());

            final List<S> mustServices = sortedServices.stream()
                .filter(it -> getPriority(it) == ServicePriority.MUST)
                .collect(Collectors.toList());

            S selected;

            if (mustServices.size() == 1) {
                selected = mustServices.get(0);
                AuroraCore.printer.accept("§8+-----------------------------------------------------");
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a发现多个 §e" + friendlyName() + " " + path + " §a服务");
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a已选择 §e" + selected.resourcePath() + " " + selected.getClass().getName());
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a因为 §e优先级为 §aMUST");
                AuroraCore.printer.accept("§8+-----------------------------------------------------");
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a未使用的服务：");
                sortedServices.stream()
                    .filter(s -> s != selected)
                    .forEach(s -> AuroraCore.printer.accept("§8|§e [AuroraCore] §e" + s.resourcePath() + " " + s.getClass().getName()));
                AuroraCore.printer.accept("§8+-----------------------------------------------------");
                return selected;
            } else if (mustServices.size() > 1) {
                AuroraCore.printer.accept("§8+-----------------------------------------------------");
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a发现多个 §e" + friendlyName() + " " + path + " §a服务");
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a无法选择服务，因为有多个优先级为 §eMUST §a服务");
                mustServices.forEach(s -> AuroraCore.printer.accept("§8|§e [AuroraCore] §e" + s.resourcePath() + " " + s.getClass().getName()));
                AuroraCore.printer.accept("§8+-----------------------------------------------------");
                AuroraCore.printer.accept("§8|§e [AuroraCore] §a未使用的服务：");
                sortedServices.stream()
                    .filter(s -> getPriority(s) != ServicePriority.MUST)
                    .forEach(s -> AuroraCore.printer.accept("§8|§e [AuroraCore] §e" + s.resourcePath() + " " + s.getClass().getName()));
                AuroraCore.printer.accept("§8+-----------------------------------------------------");
                throw new IllegalStateException("Multiple MUST services found: " + mustServices);
            }

            selected = sortedServices.get(0);

            AuroraCore.printer.accept("§8+-----------------------------------------------------");
            AuroraCore.printer.accept("§8|§e [AuroraCore] §a发现多个 §e" + friendlyName() + " " + path + " §a服务");
            AuroraCore.printer.accept("§8|§e [AuroraCore] §a已选择 §e" + selected.resourcePath() + " " + selected.getClass().getName());
            AuroraCore.printer.accept("§8+-----------------------------------------------------");
            AuroraCore.printer.accept("§8|§e [AuroraCore] §a未使用的服务：");
            sortedServices.stream()
                .filter(s -> s != selected)
                .forEach(s -> AuroraCore.printer.accept("§8|§e [AuroraCore] §e" + s.resourcePath() + " " + s.getClass().getName()));
            AuroraCore.printer.accept("§8+-----------------------------------------------------");
            return selected;
        }

    }
}
