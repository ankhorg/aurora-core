package org.inksnow.core.position;

import com.google.common.math.LongMath;
import org.bukkit.Location;
import org.bukkit.World;

import java.math.RoundingMode;

/**
 * Represents a position in the world.
 */
public final class BlockPosition {
    private static final BlockPosition ZERO = new BlockPosition(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    /**
     * Constructs a new BlockPosition.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private BlockPosition(int x, int y, int z) {
        // x, z limit from -33554432 to 33554432, reference: https://minecraft.fandom.com/zh/wiki/%E9%95%BF%E5%BA%A6
        // y in custom dim from -2032 to 4064, reference: https://minecraft.fandom.com/wiki/World_boundary
        if (x < -33554432 || x > 33554432) {
            throw new IllegalArgumentException("x must be in the range [-30000000, 30000000]");
        }

        if (y < -2048 || y > 4096) {
            throw new IllegalArgumentException("y must be in the range [-2032, 4064]");
        }

        if (z < -33554432 || z > 33554432) {
            throw new IllegalArgumentException("z must be in the range [-30000000, 30000000]");
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the x coordinate.
     *
     * @return the x coordinate
     */
    public int x() {
        return this.x;
    }

    /**
     * Gets the y coordinate.
     *
     * @return the y coordinate
     */
    public int y() {
        return this.y;
    }

    /**
     * Gets the z coordinate.
     *
     * @return the z coordinate
     */
    public int z() {
        return this.z;
    }

    /**
     * Returns a ChunkPosition representing the chunk this BlockPosition is in.
     *
     * @return a ChunkPosition
     */
    public ChunkPosition chunk() {
        return ChunkPosition.of(this.x >> 4, this.z >> 4);
    }

    /**
     * Returns a new EntityPosition with the coordinates of this BlockPosition.
     *
     * @return a new EntityPosition
     */
    public EntityPosition asEntity() {
        return EntityPosition.of(this.x, this.y, this.z);
    }

    /**
     * Returns a new Location with the coordinates of this BlockPosition in the given world.
     *
     * @param world the world
     * @return a new Location
     */
    public Location asLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    /**
     * Returns a new BlockPosition with the x coordinate set to the given value.
     *
     * @param x the x coordinate
     * @return a new BlockPosition
     */
    public BlockPosition withX(int x) {
        return of(x, this.y, this.z);
    }

    /**
     * Returns a new BlockPosition with the y coordinate set to the given value.
     *
     * @param y the y coordinate
     * @return a new BlockPosition
     */
    public BlockPosition withY(int y) {
        return of(this.x, y, this.z);
    }

    /**
     * Returns a new BlockPosition with the z coordinate set to the given value.
     *
     * @param z the z coordinate
     * @return a new BlockPosition
     */
    public BlockPosition withZ(int z) {
        return of(this.x, this.y, z);
    }

    /**
     * Adds the given values to the coordinates of this BlockPosition and returns a new BlockPosition.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a new BlockPosition
     */
    public BlockPosition add(int x, int y, int z) {
        return of(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Subtracts the given values from the coordinates of this BlockPosition and returns a new BlockPosition.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a new BlockPosition
     */
    public BlockPosition subtract(int x, int y, int z) {
        return of(this.x - x, this.y - y, this.z - z);
    }

    /**
     * Multiplies the coordinates of this BlockPosition by the given values and returns a new BlockPosition.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a new BlockPosition
     */
    public BlockPosition multiply(int x, int y, int z) {
        return of(this.x * x, this.y * y, this.z * z);
    }

    /**
     * Divides the coordinates of this BlockPosition by the given values and returns a new BlockPosition.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a new BlockPosition
     */
    public BlockPosition divide(int x, int y, int z) {
        return of(this.x / x, this.y / y, this.z / z);
    }

    /**
     * Returns the base chunk position of this BlockPosition.
     *
     * @return the base chunk position
     */
    public BlockPosition baseChunk() {
        if (((this.x & ~0xF) | (this.z & ~0xF)) == 0) {
            return this;
        }
        return of(this.x & 0xF, this.y, this.z & 0xF);
    }

    /**
     * Returns the distance squared between this BlockPosition and the given BlockPosition.
     *
     * @param pos the other BlockPosition
     * @return the distance squared
     */
    public long distanceSquared(BlockPosition pos) {
        // dx.max = dz.max = 33554432 - (-33554432) = 67108864
        // dy.max = 4096 - (-2048) = 6144
        // distance2.max = dx ^ 2 + dy ^ 2 + dz ^ 2 = 67108864 ^ 2 + 6144 ^ 2 + 67108864 ^ 2 = 9007199292489728
        // 9007199292489728 < 2 ^ 54, so it's safe to use longs
        long dx = (this.x - pos.x);
        long dy = (this.y - pos.y);
        long dz = (this.z - pos.z);
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Returns the distance between this BlockPosition and the given BlockPosition.
     *
     * @param pos the other BlockPosition
     * @return the distance
     */
    public long distance(BlockPosition pos) {
        return LongMath.sqrt(distanceSquared(pos), RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockPosition)) return false;

        BlockPosition that = (BlockPosition) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "BlockPosition{x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    /**
     * Creates a new BlockPosition with the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a new BlockPosition with the given coordinates
     */
    public static BlockPosition of(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return ZERO;
        }
        return new BlockPosition(x, y, z);
    }

    /**
     * Creates a new BlockPosition from the given Location.
     *
     * @param location the location
     * @return a new BlockPosition
     */
    public static BlockPosition fromLocation(Location location) {
        return of(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
