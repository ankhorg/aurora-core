package org.inksnow.core.impl.spi.item;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.spi.item.ItemProvider;
import org.inksnow.core.spi.item.SaveableItemProvider;

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
        final File file = new File(SAVE_DIRECTORY, key + ".yml");
        if (!file.exists()) {
            final @Nullable File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                Preconditions.checkState(parentFile.mkdirs(),
                    "Failed to create directory: %s", parentFile);
            }
        }
        final YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("item", itemStack);
        configuration.save(file);
    }

    public static final class Factory implements ItemProvider.Factory {
        private static final ResourcePath PATH = ResourcePath.of("aurora:ymlfile");

        @Override
        public @Nullable ItemProvider get(String key) {
            final File file = new File(SAVE_DIRECTORY, key + ".yml");
            if (!file.exists()) {
                return null;
            }

            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            final ItemStack itemStack = configuration.getItemStack("item");
            if (itemStack == null) {
                return null;
            }
            return new YmlFileItemProvider(key, itemStack);
        }

        @Override
        public ResourcePath resourcePath() {
            return PATH;
        }
    }
}
