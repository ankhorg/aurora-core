package org.inksnow.core.position;

/**
 * Represents a position in the world.
 */
public final class ChunkPosition {
    private final int x;
    private final int z;

    /**
     * Constructs a new ChunkPosition.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     */
    private ChunkPosition(int x, int z) {
        // reference org.inksnow.core.position.BlockPosition.BlockPosition
        // x, z limit from -2097152 to 2097152, by 16 times of the block position limit
        if (x < -2097152 || x > 2097152) {
            throw new IllegalArgumentException("x must be in the range [-2097152, 2097152]");
        }

        if (z < -2097152 || z > 2097152) {
            throw new IllegalArgumentException("z must be in the range [-2097152, 2097152]");
        }

        this.x = x;
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
     * Gets the z coordinate.
     *
     * @return the z coordinate
     */
    public int z() {
        return this.z;
    }

    /**
     * Returns a new ChunkPosition with the x coordinate set to the given value.
     *
     * @param x the x coordinate
     * @return a new ChunkPosition
     */
    public ChunkPosition withX(int x) {
        return new ChunkPosition(x, this.z);
    }

    /**
     * Returns a new ChunkPosition with the z coordinate set to the given value.
     *
     * @param z the z coordinate
     * @return a new ChunkPosition
     */
    public ChunkPosition withZ(int z) {
        return new ChunkPosition(this.x, z);
    }

    /**
     * Adds the given values to the coordinates of this ChunkPosition and returns a new ChunkPosition.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return a new ChunkPosition
     */
    public ChunkPosition add(int x, int z) {
        return new ChunkPosition(this.x + x, this.z + z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkPosition)) return false;

        ChunkPosition that = (ChunkPosition) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "ChunkPosition{x=" + x + ", z=" + z + '}';
    }

    /**
     * Returns a new ChunkPosition with the given coordinates.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return a new ChunkPosition
     */
    public static ChunkPosition of(int x, int z) {
        return new ChunkPosition(x, z);
    }
}
