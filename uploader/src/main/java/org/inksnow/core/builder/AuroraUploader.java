package org.inksnow.core.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.inksnow.cputil.HexUtil;
import org.inksnow.cputil.UncheckUtil;
import org.inksnow.cputil.download.DownloadEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Consumer;

public class AuroraUploader {
  public static final String UPLOAD_BASE_URL = "https://s0.blobs.inksnow.org/aurora/core/";
  public static final String PUBLIC_BASE_URL = "https://r.irepo.space/aurora/core/";

  public static String checksum(Path path) throws IOException {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw UncheckUtil.uncheckThrow(e);
    }
    try (InputStream in = Files.newInputStream(path)) {
      byte[] buffer = new byte[4096];
      int read;
      while ((read = in.read(buffer)) != -1) {
        digest.update(buffer, 0, read);
      }
    }

    return HexUtil.of().formatHex(digest.digest());
  }

  @SneakyThrows
  public static void main(String[] args) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    RuntimeManifest.Builder builder = RuntimeManifest.builder();

    Files.walkFileTree(Paths.get("api/build/libs"), new UploadVisitor(builder::api));

    Files.walkFileTree(Paths.get("build/libs"), new UploadVisitor(builder::impl));
    Files.walkFileTree(Paths.get("build/runtimeLibs"), new UploadVisitor(builder::impl));

    RuntimeManifest manifest = builder.build();
    Path manifestPath = Paths.get("build/runtimeManifest.json");
    try (Writer writer = Files.newBufferedWriter(manifestPath, StandardCharsets.UTF_8)) {
      gson.toJson(manifest, writer);
    }

    upload(manifestPath, UPLOAD_BASE_URL + "manifest.json", true);
  }

  @SneakyThrows
  public static void upload(Path path, String url, boolean override) {
    String username = System.getenv("IREPO_USERNAME");
    String password = System.getenv("IREPO_PASSWORD");

    if (!override) {
      HttpURLConnection testExistConnection = (HttpURLConnection) new URL(url).openConnection();
      try {
        if (testExistConnection.getResponseCode() == 200) {
          return;
        }
        System.out.println("Uploading: " + testExistConnection.getResponseCode());
      } finally {
        testExistConnection.disconnect();
      }
    }

    HttpURLConnection uploadConnection = (HttpURLConnection) new URL(url).openConnection();
    try {
      uploadConnection.setRequestMethod("PUT");

      String auth = username + ":" + password;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
      uploadConnection.setRequestProperty("Authorization", "Basic " + encodedAuth);

      uploadConnection.setDoOutput(true);
      uploadConnection.setDoInput(true);

      try (OutputStream out = uploadConnection.getOutputStream()) {
        Files.copy(path, out);
      }

      if (uploadConnection.getResponseCode() != 200) {
        System.out.println("Failed to upload: " + uploadConnection.getResponseCode());
      } else {
        System.out.println("Uploaded: " + url);
      }
    } finally {
      uploadConnection.disconnect();
    }
  }

  @RequiredArgsConstructor
  private static final class UploadVisitor extends SimpleFileVisitor<Path> {
    private final Consumer<DownloadEntry> consumer;

    @Override
    @SneakyThrows
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
      if (file.getFileName().toString().endsWith("-sources.jar")) {
        return FileVisitResult.CONTINUE;
      }
      String hash = checksum(file);
      upload(file, UPLOAD_BASE_URL + hash.substring(0, 2) + "/" + hash, false);
      consumer.accept(new DownloadEntry(
          file.getFileName().toString(),
          PUBLIC_BASE_URL + hash.substring(0, 2) + "/" + hash,
          hash
      ));
      return FileVisitResult.CONTINUE;
    }
  }
}
