package org.inksnow.core.builder;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.inksnow.cputil.download.DownloadEntry;

import java.util.List;

@Value
@Builder
public class RuntimeManifest {
    @Singular("api")
    List<DownloadEntry> api;

    @Singular("impl")
    List<DownloadEntry> impl;

    @Singular("parentOnly")
    List<String> parentOnly;
    @Singular("selfOnly")
    List<String> selfOnly;
    @Singular("parentThenSelf")
    List<String> parentThenSelf;
    @Singular("selfThenParent")
    List<String> selfThenParent;
    @Singular("disabled")
    List<String> disabled;
}
