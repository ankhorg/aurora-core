package org.inksnow.core.impl.spi;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public final class AuroraServiceLoader<S> implements Iterable<S> {
  private final @NonNull Class<S> service;
  private final @NonNull Map<String, S> providers = new LinkedHashMap<>();
  private final @NonNull Map<String, Throwable> errors = new LinkedHashMap<>();

  public AuroraServiceLoader(@NonNull Class<S> service) {
    this.service = service;
  }

  public List<S> load(ClassLoader classLoader) {
    List<S> list = new ArrayList<>();
    try {
      loadImpl(list, classLoader);
    } catch (IOException e) {
      logger.error("Failed to load service: {}", service.getName(), e);
    }
    return list;
  }

  private void loadImpl(List<S> resultConsume, ClassLoader classLoader) throws IOException {
    Enumeration<URL> resources = classLoader.getResources("META-INF/services/" + service.getName());
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      loadServiceFile(resultConsume, classLoader, url);
    }
  }

  private void loadServiceFile(List<S> resultConsume, ClassLoader classLoader, URL url) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
      String line;
      lineLoop: while ((line = reader.readLine()) != null) {
        int ci = line.indexOf('#');
        if (ci >= 0) {
          line = line.substring(0, ci);
        }
        line = line.trim();
        int n = line.length();
        if (n == 0) {
          continue;
        }
        if ((line.indexOf(' ') >= 0) || (line.indexOf('\t') >= 0)) {
          logger.error("Illegal service provider class name in {}: '{}'", url, line);
          continue;
        }
        int cp = line.codePointAt(0);
        if (!Character.isJavaIdentifierStart(cp)) {
          logger.error("Illegal service provider class name in {}: '{}'", url, line);
          continue;
        }
        for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
          cp = line.codePointAt(i);
          if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
            logger.error("Illegal service provider class name in {}: '{}'", url, line);
            continue lineLoop;
          }
        }
        if (!providers.containsKey(line)) {
          loadServiceClass(resultConsume, classLoader, line);
        }
      }
    } catch (IOException e) {
      logger.error("Failed to load service file: {}", url, e);
    }
  }

  private void loadServiceClass(List<S> resultConsume, ClassLoader classLoader, String className) {
    try {
      Class<?> clazz = Class.forName(className, false, classLoader);
      if (!service.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Service class " + className + " does not implement " + service.getName());
      }
      S provider = service.cast(clazz.getConstructor().newInstance());
      providers.put(className, provider);
      resultConsume.add(provider);
    } catch (Exception e) {
      logger.error("Failed to load service class: {}", className, e);
      errors.put(className, e);
    }
  }

  @Override
  public Iterator<S> iterator() {
    return providers.values().iterator();
  }
}
