package org.inksnow.core.loader;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class AuroraLoaderConfig {
  String updateCenter;

  boolean useLocalVersion;
  List<String> localApiPaths;
  List<String> localImplPaths;

  public static AuroraLoaderConfig createDefault() {
    return new AuroraLoaderConfig(
        "https://r.irepo.space/aurora/core/manifest.json",
        false,
        Collections.emptyList(),
        Collections.emptyList()
    );
  }
}
