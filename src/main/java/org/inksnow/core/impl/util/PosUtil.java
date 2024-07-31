package org.inksnow.core.impl.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.inksnow.core.position.BlockPosition;

@UtilityClass
public class PosUtil {

    /**
     * Get the block ID of a location.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the block ID
     */
    public static int locationXYZ_blockId(int x, int y, int z) {
        return (y << 8) | ((z & 0b1111) << 4) | (x & 0b1111);
    }

    /**
     * Get the block ID of a location.
     *
     * @param location the location
     * @return the block ID
     */
    public static int location_blockId(Location location) {
        return locationXYZ_blockId(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get the X coordinate of a block from the block ID.
     *
     * @param blockId the block ID
     * @return the X coordinate of the block
     */
    public static int blockId_blockX(int blockId) {
        return blockId & 0b1111;
    }

    /**
     * Get the Y coordinate of a block from the block ID.
     *
     * @param blockId the block ID
     * @return the Y coordinate of the block
     */
    public static int blockId_blockY(int blockId) {
        return blockId >> 8;
    }

    /**
     * Get the Z coordinate of a block from the block ID.
     *
     * @param blockId the block ID
     * @return the Z coordinate of the block
     */
    public static int blockId_blockZ(int blockId) {
        return (blockId >> 4) & 0b1111;
    }

    /**
     * Get the chunk X coordinate from the X coordinate of a location.
     *
     * @param x the X coordinate of the location
     * @return the chunk X coordinate
     */
    public static int locationX_chunkX(int x) {
        return x >> 4;
    }

    /**
     * Get the chunk Z coordinate from the X coordinate of a location.
     *
     * @param z the Z coordinate of the location
     * @return the chunk Z coordinate
     */
    public static int locationZ_chunkZ(int z) {
        return z >> 4;
    }

    /**
     * Get the region X coordinate from the chunk X coordinate.
     *
     * @param x the chunk X coordinate
     * @return the region X coordinate
     */
    public static int chunkX_regionX(int x) {
        return x >> 5;
    }

    /**
     * Get the region Z coordinate from the chunk Z coordinate.
     *
     * @param z the chunk Z coordinate
     * @return the region Z coordinate
     */
    public static int chunkZ_regionZ(int z) {
        return z >> 5;
    }

    /**
     * Get the region ID from the region X and Z coordinates.
     *
     * @param regionX the region X coordinate
     * @param regionZ the region Z coordinate
     * @return the region ID
     */
    public static long regionXZ_regionId(int regionX, int regionZ) {
        return (((long) regionX) & 0xffffffffL) | ((((long) regionZ) & 0xffffffffL) << 32);
    }

    /**
     * Get the region ID from the chunk x and z coordinates.
     *
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return the region ID
     */
    public static long chunkXZ_regionId(int chunkX, int chunkZ) {
        return (((long) chunkX_regionX(chunkX)) & 0xffffffffL) | ((((long) chunkZ_regionZ(chunkZ)) & 0xffffffffL) << 32);
    }

    /**
     * Get the region ID from the chunk.
     *
     * @param chunk the chunk
     * @return the region ID
     */
    public static long chunk_regionId(Chunk chunk) {
        return chunkXZ_regionId(chunk.getX(), chunk.getZ());
    }

    public static int chunkX_regionLocalX(int x) {
        return x & 0b11111;
    }

    public static int chunkZ_regionLocalZ(int z) {
        return z & 0b11111;
    }

    public static int chunkXZ_regionLocalId(int x, int z) {
        return (x & 0b11111) | ((z & 0b11111) << 5);
    }

    public static int chunk_regionLocalId(Chunk chunk) {
        return (chunk.getX() & 0b11111) | ((chunk.getZ() & 0b11111) << 5);
    }

    /**
     * Get the region X coordinate from the region ID.
     *
     * @param regionId the region ID
     * @return the region X coordinate
     */
    public static int regionId_regionX(long regionId) {
        return (int) (regionId & 0xffffffffL);
    }

    /**
     * Get the region Z coordinate from the region ID.
     *
     * @param regionId the region ID
     * @return the region Z coordinate
     */
    public static int regionId_regionZ(long regionId) {
        return (int) (regionId >> 32);
    }

    public static boolean isInChunk(int x, int z, int chunkX, int chunkZ) {
        return x >= chunkX << 4 && x < (chunkX + 1) << 4
                && z >= chunkZ << 4 && z < (chunkZ + 1) << 4;
    }
}
