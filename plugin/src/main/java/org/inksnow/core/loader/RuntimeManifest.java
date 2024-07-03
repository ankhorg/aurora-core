package org.inksnow.core.loader;

import lombok.Value;
import org.inksnow.cputil.download.DownloadEntry;

import java.util.List;

@Value
public class RuntimeManifest {
    List<DownloadEntry> api;
    List<DownloadEntry> impl;

    List<String> parentOnly;
    List<String> selfOnly;
    List<String> parentThenSelf;
    List<String> selfThenParent;
    List<String> disabled;
}
