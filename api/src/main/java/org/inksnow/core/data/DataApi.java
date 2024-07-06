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

public interface DataApi {

    BlockDataHolder of(Block block);

    ChunkDataHolder of(Chunk chunk);

    EntityDataHolder of(Entity entity);

    ItemDataHolder of(ItemStack itemStack);

    UserDataHolder of(Player player);

    WorldDataHolder of(World world);
}
