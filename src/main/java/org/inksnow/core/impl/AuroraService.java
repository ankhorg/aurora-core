package org.inksnow.core.impl;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.spi.AbstractSpiRegistry;
import org.inksnow.core.impl.spi.item.AuroraItemSpi;
import org.inksnow.core.impl.spi.worldtag.AuroraWorldTagSpi;
import org.inksnow.core.impl.util.IdentityBox;
import org.inksnow.core.impl.util.TestableUtil;
import org.inksnow.core.resource.WithResourcePath;
import org.inksnow.core.spi.ServiceApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class AuroraService implements ServiceApi {
    @Getter
    private final AuroraItemSpi item;
    @Getter
    private final AuroraWorldTagSpi worldTag;

    private final Map<IdentityBox<Plugin>, Boolean> pluginScanned;
    private final AbstractSpiRegistry<?>[] registries;

    @Inject
    private AuroraService(AuroraItemSpi item, AuroraWorldTagSpi worldTag) {
        this.item = item;
        this.worldTag = worldTag;
        this.pluginScanned = new WeakHashMap<>();
        this.registries = new AbstractSpiRegistry<?>[]{
                item, worldTag
        };
    }

    private static <S extends WithResourcePath> void register(AbstractSpiRegistry<S> registry, ClassLoader classLoader, List<String> pendingMessages) {
        final List<S> services = registry.loader().load(classLoader);
        for (S service : services) {
            if (!TestableUtil.test(service)) {
                pendingMessages.add("§8|§e [AuroraCore] §c跳过注册 §a" + registry.friendlyName() + " "
                        + service.resourcePath() + " " + service.getClass().getName() + " §c未通过测试");
                continue;
            }
            registry.register(service);
            pendingMessages.add("§8|§e [AuroraCore] 已注册 §a" + registry.friendlyName() + " "
                    + service.resourcePath() + "§e §a" + service.getClass().getName());
        }
    }

    @Override
    public void scanPlugin(Plugin targetPlugin) {
        if (pluginScanned.putIfAbsent(new IdentityBox<>(targetPlugin), true) != null) {
            return;
        }
        if (AuroraCore.serverBootstrap()) {
            AuroraCore.printer.accept("§8+-----------------------------------------------------");
            AuroraCore.printer.accept("§8|§e [AuroraCore] 发现插件热加载§a " + targetPlugin.getName() + " §e正在注册");
        }

        final List<String> pendingMessages = new ArrayList<>();

        final @Nullable ClassLoader classLoader;
        if (targetPlugin == AuroraCore.plugin) {
            classLoader = AuroraCore.class.getClassLoader();
        } else {
            classLoader = targetPlugin.getClass().getClassLoader();
        }
        if (classLoader == null) {
            pendingMessages.add("§8|§e [AuroraCore] §c跳过注册 §a" + targetPlugin.getName() + " §c: §aClassLoader 为 null");
            pendingMessages.add("§8|§e [AuroraCore] §c这通常只会在开发环境中发生，如果您在生产环境中看到此消息，请在 GitHub 上报告此问题");
        } else {
            for (AbstractSpiRegistry<?> registry : registries) {
                register(registry, classLoader, pendingMessages);
            }
        }

        if (AuroraCore.serverBootstrap()) {
            AuroraCore.printer.accept("§8+-----------------------------------------------------");
        } else if (!pendingMessages.isEmpty()) {
            AuroraCore.printer.accept("§8|§e [AuroraCore] 正在扫描插件 §a" + targetPlugin.getName());
            pendingMessages.forEach(AuroraCore.printer);
            AuroraCore.printer.accept("§8+-----------------------------------------------------");
        }
    }
}
