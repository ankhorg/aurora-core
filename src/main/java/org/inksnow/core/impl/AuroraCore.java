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
import org.inksnow.ankhinvoke.AnkhInvoke;
import org.inksnow.ankhinvoke.bukkit.asm.NmsVersionRemapper;
import org.inksnow.ankhinvoke.bukkit.paper.PaperEnvironment;
import org.inksnow.ankhinvoke.classpool.ClassLoaderPoolLoader;
import org.inksnow.ankhinvoke.classpool.LoadedClassPoolLoader;
import org.inksnow.ankhinvoke.classpool.ResourcePoolLoader;
import org.inksnow.ankhinvoke.reference.ResourceReferenceSource;
import org.inksnow.core.Aurora;
import org.inksnow.core.AuroraApi;
import org.inksnow.core.data.provider.DataProviderRegistrator;
import org.inksnow.core.impl.data.AuroraData;
import org.inksnow.core.impl.data.provider.AuroraDataProviderRegistrator;
import org.inksnow.core.impl.data.registration.EntityRegistration;
import org.inksnow.core.impl.data.store.player.AuroraPlayerDataService;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.util.Builder;
import org.inksnow.cputil.logger.AuroraLoggerFactory;
import org.inksnow.cputil.logger.impl.parent.AuroraParentLogger;

import java.net.URLClassLoader;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class AuroraCore implements AuroraApi, Listener {

    public static final int FLUSH_PLAYER_DATA_INTERVAL = 20 * 60 * 5;

    public static final Consumer<String> printer = Bukkit.getConsoleSender()::sendMessage;
    /* package-private */ static @MonotonicNonNull JavaPlugin plugin;
    private static @MonotonicNonNull AuroraCore instance;
    @Getter
    private static boolean serverBootstrap = false;

    static {
        AuroraLoggerFactory.instance().nameMapping(name->{
            if (name.startsWith("org.inksnow.core.impl.")) {
                return "AuroraCore " + name.substring("org.inksnow.core.impl.".length());
            } else {
                return name;
            }
        });
        AuroraLoggerFactory.instance().provider(new AuroraParentLogger("aurora.core.loader.slf4j"));

        final @Nullable ClassLoader classLoader = AuroraCore.class.getClassLoader();
        Preconditions.checkState(classLoader != null, "AuroraCore classLoader is bootstrap class loader");
        Preconditions.checkState(classLoader instanceof URLClassLoader, "AuroraCore classLoader is not URLClassLoader");
        final URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

        final AnkhInvoke.Builder builder = AnkhInvoke.builder()
                .inject()
                /**/.classLoaderProvider(urlClassLoader)
                /**/.urlInjector(urlClassLoader)
                /**/.build()
                .classPool()
                /**/.appendLoader(new LoadedClassPoolLoader(classLoader))
                /**/.appendLoader(new ResourcePoolLoader(classLoader))
                /**/.appendLoader(new ClassLoaderPoolLoader(classLoader))
                /**/.build()
                .reference()
                /**/.appendPackage("org.inksnow.core.impl.ref")
                /**/.appendSource(new ResourceReferenceSource(urlClassLoader))
                /**/.build()
                .referenceRemap()
                /**/.append(new NmsVersionRemapper())
                /**/.appendRegistry()
                /**/.setApplyMapRegistry("aurora-core")
                /**/.build();
        if (PaperEnvironment.hasPaperMapping()) {
            builder.referenceRemap()
                    .append(PaperEnvironment.createRemapper());
        }
        builder.build();
    }

    private final Injector injector;


    @Getter
    private final AuroraService service;

    @Getter
    private final AuroraData data;

    public static AuroraCore instance() {
        @Nullable AuroraCore instance = AuroraCore.instance;
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

    @SuppressWarnings("unused") // used by reflect in module plugin
    public static void onInit(JavaPlugin plugin) {
        Preconditions.checkState(instance == null, "AuroraCore already initialized");
        AuroraCore.plugin = plugin;
        instance();
    }

    @SuppressWarnings("unused") // used by reflect in module plugin
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

    @SuppressWarnings("unused") // used by reflect in module plugin
    public static void onEnable(JavaPlugin plugin) {
        Preconditions.checkState(instance != null, "AuroraCore not initialized");

        printer.accept("§8+-----------------------------------------------------");
        printer.accept("§8|§e [AuroraCore] 正在加载§a AuroraCore " + plugin.getDescription().getVersion());

        Bukkit.getPluginManager().registerEvents(instance, plugin);
        Bukkit.getPluginManager().registerEvents(instance.injector.getInstance(AuroraWorldDataService.class), plugin);
        Bukkit.getPluginManager().registerEvents(instance.injector.getInstance(AuroraPlayerDataService.class), plugin);
        Bukkit.getScheduler().runTask(plugin, () -> serverBootstrap = true);
        Bukkit.getScheduler().runTaskTimer(plugin,
                () -> instance.injector.getInstance(AuroraPlayerDataService.class).flush(),
                FLUSH_PLAYER_DATA_INTERVAL, FLUSH_PLAYER_DATA_INTERVAL
        );

        new EntityRegistration().register(new AuroraDataProviderRegistrator());

        printer.accept("§8+-----------------------------------------------------");
        for (Plugin scanPlugin : Bukkit.getPluginManager().getPlugins()) {
            instance.onPluginEnabled(new PluginEnableEvent(scanPlugin));
        }
    }

    @SuppressWarnings("unused") // used by reflect in module plugin
    public static void onDisable(JavaPlugin plugin) {
        printer.accept("§8+-----------------------------------------------------");
        printer.accept("§8|§e [AuroraCore] 正在保存世界数据...");
        instance().injector
                .getInstance(AuroraWorldDataService.class)
                .close();
        printer.accept("§8|§e [AuroraCore] 正在保存玩家数据...");
        instance().injector
                .getInstance(AuroraPlayerDataService.class)
                .close();
        printer.accept("§8+-----------------------------------------------------");
    }

    @SuppressWarnings("unused") // used by reflect in module plugin
    public static boolean onCommand(JavaPlugin plugin, CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @SuppressWarnings("unused") // used by reflect in module plugin
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
    @SuppressWarnings("override.return")
    public <@NonNull T> T getFactory(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
