package org.inksnow.core.impl.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.inksnow.core.api.item.ItemProvider;
import org.inksnow.core.api.item.SaveableItemProvider;

import java.io.File;

@RequiredArgsConstructor
public final class YmlFileItemProvider implements SaveableItemProvider {
  public static final String SAVE_DIRECTORY = "./plugins/AuroraCore/serialize/items";

  private final String key;
  private final ItemStack itemStack;

  @Override
  public ItemStack create() {
    return itemStack.clone();
  }

  @SneakyThrows
  @Override
  public void save() {
    File file = new File(SAVE_DIRECTORY, key + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();
    }
    YamlConfiguration configuration = new YamlConfiguration();
    configuration.set("item", itemStack);
    configuration.save(file);
  }

  public static final class Factory implements ItemProvider.Factory {
    @Override
    public ItemProvider get(String key) {
      File file = new File(SAVE_DIRECTORY, key + ".yml");
      if (!file.exists()) {
        return null;
      }

      YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
      ItemStack itemStack = configuration.getItemStack("item");
      if (itemStack == null) {
        return null;
      }
      return new YmlFileItemProvider(key, itemStack);
    }

    @Override
    public String namespace() {
      return "aurora:ymlfile";
    }
  }
}
