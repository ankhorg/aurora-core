package org.inksnow.core.data;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.inksnow.core.data.holder.BlockDataHolder;
import org.inksnow.core.data.holder.ChunkDataHolder;
import org.inksnow.core.data.holder.EntityDataHolder;
import org.inksnow.core.data.holder.ItemDataHolder;
import org.inksnow.core.data.holder.UserDataHolder;
import org.inksnow.core.data.holder.WorldDataHolder;

/**
 * The data API.
 */
public interface DataApi {

    /**
     * Get the data holder of a block.
     *
     * @param location the location
     * @return the data holder
     */
    BlockDataHolder of(Location location);

    /**
     * Get the data holder of a block.
     *
     * @param block the block
     * @return the data holder
     */
    default BlockDataHolder of(Block block) {
        return of(block.getLocation());
    }

    /**
     * Get the data holder of a chunk.
     *
     * @param chunk the chunk
     * @return the data holder
     */
    ChunkDataHolder of(Chunk chunk);

    /**
     * Get the data holder of an entity.
     *
     * @param entity the entity
     * @return the data holder
     */
    EntityDataHolder of(Entity entity);

    /**
     * Get the data holder of an item stack.
     *
     * @param itemStack the item stack
     * @return the data holder
     */
    ItemDataHolder of(ItemStack itemStack);

    /**
     * Get the data holder of a player.
     *
     * @param player the player
     * @return the data holder
     */
    UserDataHolder of(Player player);

    /**
     * Get the data holder of a world.
     *
     * @param world the world
     * @return the data holder
     */
    WorldDataHolder of(World world);

    /**
     * Get the data manager
     *
     * @return the data manager
     */
    DataManager dataManager();
}
