package org.inksnow.core.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.Aurora;
import org.inksnow.core.AuroraApi;
import org.inksnow.core.util.Builder;
import org.inksnow.cputil.logger.AuroraLoggerFactory;
import org.inksnow.cputil.logger.impl.parent.AuroraParentLogger;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class AuroraCore implements AuroraApi, Listener {
    public static final Consumer<String> printer = Bukkit.getConsoleSender()::sendMessage;
    private static @MonotonicNonNull AuroraCore instance;
    /* package-private */ static @MonotonicNonNull JavaPlugin plugin;
    @Getter
    private static boolean serverBootstrap = false;

    static {
        AuroraLoggerFactory.instance().provider(new AuroraParentLogger("aurora.core.loader.slf4j"));
    }

    private final Injector injector;


    @Getter
    private final AuroraService service;

    public static AuroraCore instance() {
        AuroraCore instance = AuroraCore.instance;
        if (instance != null) {
            return instance;
        }
        synchronized (AuroraCore.class) {
            instance = AuroraCore.instance;
            if (instance != null) {
                return instance;
            }
            final Injector bootstrapInjector = Guice.createInjector(
                new AuroraCoreModule()
            );
            instance = bootstrapInjector.getInstance(AuroraCore.class);
            Aurora.api(instance);
            AuroraCore.instance = instance;
        }
        return instance;
    }

    public static void onInit(JavaPlugin plugin) {
        Preconditions.checkState(instance == null, "AuroraCore already initialized");
        AuroraCore.plugin = plugin;
        instance();
    }

    public static void onLoad(JavaPlugin plugin) {
        Preconditions.checkState(instance != null, "AuroraCore not initialized");

        printer.accept("§8+-----------------------------------------------------");
        printer.accept("§8|§e [AuroraCore] 正在加载§a AuroraCore " + plugin.getDescription().getVersion());
        printer.accept("§8|§e [AuroraCore] 作者:§a h6EX4j");
        printer.accept("§8|§e [AuroraCore] 企鹅:§a 812276666  §e 群:§a 946882957");
        printer.accept("§8+-----------------------------------------------------");
        for (Plugin scanPlugin : Bukkit.getPluginManager().getPlugins()) {
            instance.onPluginEnabled(new PluginEnableEvent(scanPlugin));
        }
    }

    public static void onEnable(JavaPlugin plugin) {
        Preconditions.checkState(instance != null, "AuroraCore not initialized");

        printer.accept("§8+-----------------------------------------------------");
        printer.accept("§8|§e [AuroraCore] 正在加载§a AuroraCore " + plugin.getDescription().getVersion());

        Bukkit.getPluginManager().registerEvents(instance, plugin);
        Bukkit.getScheduler().runTask(plugin, () -> serverBootstrap = true);

        printer.accept("§8+-----------------------------------------------------");
        for (Plugin scanPlugin : Bukkit.getPluginManager().getPlugins()) {
            instance.onPluginEnabled(new PluginEnableEvent(scanPlugin));
        }
    }

    public static void onDisable(JavaPlugin plugin) {
    }

    public static boolean onCommand(JavaPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    public static @Nullable List<String> onTabComplete(JavaPlugin plugin, CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    @EventHandler
    public void onPluginEnabled(PluginEnableEvent event) {
        service.scanPlugin(event.getPlugin());
    }

    @Override
    public <B extends Builder<?, ?>> B createBuilder(Class<B> clazz) {
        if (!Builder.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Builder class must implement Builder interface");
        }
        return injector.getInstance(clazz);
    }

    @Override
    public <@NonNull T> T getFactory(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
