package org.inksnow.core.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.inksnow.cputil.AuroraDownloader;
import org.inksnow.cputil.UnsafeUtil;
import org.inksnow.cputil.classloader.AuroraClassLoader;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class AuroraCorePlugin extends JavaPlugin {
  private static final String AURORA_CORE_CLASS_NAME = "org.inksnow.core.impl.AuroraCore";
  private static final Class<?> AURORA_CORE_CLASS = loadCoreClass();

  private static final MethodHandle ON_INIT = loadMethod("onInit", void.class, JavaPlugin.class);
  private static final MethodHandle ON_LOAD = loadMethod("onLoad", void.class, JavaPlugin.class);
  private static final MethodHandle ON_ENABLE = loadMethod("onEnable", void.class, JavaPlugin.class);
  private static final MethodHandle ON_DISABLE = loadMethod("onDisable", void.class, JavaPlugin.class);

  private static final MethodHandle ON_COMMAND = loadMethod("onCommand", boolean.class, JavaPlugin.class, CommandSender.class, Command.class, String.class, String[].class);
  private static final MethodHandle ON_TAB_COMPLETE = loadMethod("onTabComplete", List.class, JavaPlugin.class, CommandSender.class, Command.class, String.class, String[].class);

  @SneakyThrows
  private static Class<?> loadCoreClass() {
    Gson gson = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();
    Path configPath = Paths.get("plugins", "AuroraCore", "loader-config.json");
    AuroraLoaderConfig config;
    if (configPath.toFile().exists()) {
      try (Reader reader = Files.newBufferedReader(configPath)) {
        config = gson.fromJson(reader, AuroraLoaderConfig.class);
      }
    } else {
      config = AuroraLoaderConfig.createDefault();
      Files.createDirectories(configPath.getParent());
      Files.write(configPath, gson.toJson(config).getBytes());
    }

    URLClassLoader pluginClassLoader = (URLClassLoader) AuroraCorePlugin.class.getClassLoader();
    AuroraClassLoader.Builder implClassLoaderBuilder = AuroraClassLoader.builder()
        .parent(pluginClassLoader);

    MethodHandle ADD_URL = UnsafeUtil.lookup().findVirtual(URLClassLoader.class, "addURL", MethodType.methodType(void.class, java.net.URL.class));
    Consumer<Path> addApiPath = new Consumer<Path>() {
      @Override
      @SneakyThrows
      public void accept(Path path) {
        ADD_URL.invoke(pluginClassLoader, path.toUri().toURL());
      }
    };
    Consumer<Path> addImplPath = new Consumer<Path>() {
      @Override
      @SneakyThrows
      public void accept(Path path) {
        implClassLoaderBuilder.url(path.toUri().toURL());
      }
    };

    if (config.useLocalVersion()) {
      for (String apiPath : config.localApiPaths()) {
        try (Stream<Path> stream = Files.list(Paths.get(apiPath))) {
          stream.forEach(addApiPath);
        }
      }
      for (String implPath : config.localImplPaths()) {
        try (Stream<Path> stream = Files.list(Paths.get(implPath))) {
          stream.forEach(addImplPath);
        }
      }
    } else {
      RuntimeManifest runtimeManifest;
      try (Reader reader = new InputStreamReader(new URL(config.updateCenter() + "/api").openStream(), StandardCharsets.UTF_8)) {
        runtimeManifest = gson.fromJson(reader, RuntimeManifest.class);
      }
      AuroraDownloader downloader = new AuroraDownloader(Paths.get("plugins", ".aurora"));
      downloader.downloadAll(runtimeManifest.api()).forEach(addApiPath);
      downloader.downloadAll(runtimeManifest.impl()).forEach(addImplPath);
    }

    return Class.forName(AURORA_CORE_CLASS_NAME, false, implClassLoaderBuilder.build());
  }

  @SneakyThrows
  private static MethodHandle loadMethod(String methodName, Class<?> rType, Class<?> ... argTypes) {
    MethodHandle methodHandle = MethodHandles.lookup().findStatic(AURORA_CORE_CLASS, methodName, MethodType.methodType(rType, argTypes));
    if (methodHandle.isVarargsCollector()) {
      return methodHandle.asFixedArity();
    } else {
      return methodHandle;
    }
  }

  @SneakyThrows
  public AuroraCorePlugin() {
    ON_INIT.invokeExact((JavaPlugin) this);
  }

  @SneakyThrows
  public AuroraCorePlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);

    ON_LOAD.invokeExact((JavaPlugin) this);
  }

  @Override
  @SneakyThrows
  public void onLoad() {
    ON_LOAD.invokeExact((JavaPlugin) this);
  }

  @Override
  @SneakyThrows
  public void onEnable() {
    ON_ENABLE.invokeExact((JavaPlugin) this);
  }

  @Override
  @SneakyThrows
  public void onDisable() {
    ON_DISABLE.invokeExact((JavaPlugin) this);
  }

  @Override
  @SneakyThrows
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return (boolean) ON_COMMAND.invokeExact((JavaPlugin) this, sender, command, label, args);
  }

  @Override
  @SneakyThrows
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return (List<String>) ON_TAB_COMPLETE.invokeExact((JavaPlugin) this, sender, command, alias, args);
  }
}
