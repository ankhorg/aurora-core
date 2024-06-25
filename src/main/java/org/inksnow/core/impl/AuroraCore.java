package org.inksnow.core.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inksnow.core.api.Aurora;
import org.inksnow.core.api.AuroraApi;
import org.inksnow.core.api.spi.Namespaced;
import org.inksnow.core.impl.item.AuroraItemSpi;
import org.inksnow.core.impl.spi.AbstractSpiRegistry;
import org.inksnow.core.impl.util.IdentityBox;
import org.inksnow.core.impl.util.TestableUtil;
import org.inksnow.core.impl.worldtag.AuroraWorldTagSpi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuroraCore implements AuroraApi, Listener {
  private static final @NonNull Consumer<@NonNull String> printer = Bukkit.getConsoleSender()::sendMessage;
  private static AuroraCore instance;

  private final @NonNull Map<@NonNull IdentityBox<@NonNull Plugin>, @NonNull Boolean> pluginScanned = new WeakHashMap<>();
  private final @NonNull JavaPlugin plugin;
  @Getter
  private final @NonNull AuroraItemSpi item = new AuroraItemSpi();
  @Getter
  private final @NonNull AuroraWorldTagSpi worldTag = new AuroraWorldTagSpi();
  private final @NonNull AbstractSpiRegistry @NonNull [] registries = {
      item, worldTag
  };

  private boolean serverBootstrap = false;

  public static void onInit(@NonNull JavaPlugin plugin) {
    instance = new AuroraCore(plugin);
    Aurora.api(instance);
  }

  public static void onLoad(@NonNull JavaPlugin plugin) {
    printer.accept("§8+-----------------------------------------------------");
    printer.accept("§8|§e [AuroraCore] 正在加载§a AuroraCore " + plugin.getDescription().getVersion());
    printer.accept("§8|§e [AuroraCore] 作者:§a h6EX4j");
    printer.accept("§8|§e [AuroraCore] 企鹅:§a 812276666  §e 群:§a 946882957");
    printer.accept("§8+-----------------------------------------------------");

    for (Plugin scanPlugin : Bukkit.getPluginManager().getPlugins()) {
      instance.onPluginEnabled(new PluginEnableEvent(scanPlugin));
    }
  }

  public static void onEnable(@NonNull JavaPlugin plugin) {
    printer.accept("§8+-----------------------------------------------------");
    printer.accept("§8|§e [AuroraCore] 正在加载§a AuroraCore " + plugin.getDescription().getVersion());

    Bukkit.getPluginManager().registerEvents(instance, plugin);
    Bukkit.getScheduler().runTask(plugin, () -> instance.serverBootstrap = true);

    printer.accept("§8+-----------------------------------------------------");
    for (Plugin scanPlugin : Bukkit.getPluginManager().getPlugins()) {
      instance.onPluginEnabled(new PluginEnableEvent(scanPlugin));
    }
  }

  public static void onDisable(@NonNull JavaPlugin plugin) {
  }

  public static boolean onCommand(@NonNull JavaPlugin plugin, @NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String @NonNull [] args) {
    return false;
  }

  public static List<@NonNull String> onTabComplete(JavaPlugin plugin, @NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String @NonNull [] args) {
    return null;
  }

  @Override
  public void scanPlugin(@NonNull Plugin targetPlugin) {
    if (pluginScanned.putIfAbsent(new IdentityBox<>(plugin), true) != null) {
      return;
    }
    if (serverBootstrap) {
      printer.accept("§8+-----------------------------------------------------");
      printer.accept("§8|§e [AuroraCore] 发现插件热加载§a " + targetPlugin.getName() + " §e正在注册");
    }

    List<String> pendingMessages = new ArrayList<>();

    ClassLoader classLoader;
    if (plugin == targetPlugin) {
      classLoader = AuroraCore.class.getClassLoader();
    } else {
      classLoader = targetPlugin.getClass().getClassLoader();
    }

    for (AbstractSpiRegistry<Namespaced> registry : registries) {
      List<? extends Namespaced> services = registry.loader().load(classLoader);
      for (Namespaced service : services) {
        if (!TestableUtil.test(service)) {
          pendingMessages.add("§8|§e [AuroraCore] §c跳过注册 §a" + registry.friendlyName() + " "
              + service.namespace() + "§c: §a" + service.getClass().getName() + " §c未通过测试");
          continue;
        }
        registry.registerForce(service);
        pendingMessages.add("§8|§e [AuroraCore] 已注册 §a" + registry.friendlyName() + " "
            + service.namespace() + "§e: §a" + service.getClass().getName());
      }
    }

    if (serverBootstrap) {
      printer.accept("§8+-----------------------------------------------------");
    } else if (!pendingMessages.isEmpty()) {
      printer.accept("§8|§e [AuroraCore] 正在扫描插件 §a" + targetPlugin.getName());
      pendingMessages.forEach(printer);
      printer.accept("§8+-----------------------------------------------------");
    }
  }

  @EventHandler
  public void onPluginEnabled(@NonNull PluginEnableEvent event) {
    scanPlugin(event.getPlugin());
  }
}
