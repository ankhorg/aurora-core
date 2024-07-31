package org.inksnow.core.position;

import org.bukkit.Location;

/**
 * Represents a position in the world.
 */
public final class EntityPosition {
    private final double x;
    private final double y;
    private final double z;

    /**
     * Constructs a new EntityPosition.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private EntityPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the x coordinate.
     *
     * @return the x coordinate
     */
    public double x() {
        return this.x;
    }

    /**
     * Gets the y coordinate.
     *
     * @return the y coordinate
     */
    public double y() {
        return this.y;
    }

    /**
     * Gets the z coordinate.
     *
     * @return the z coordinate
     */
    public double z() {
        return this.z;
    }

    /**
     * Returns a ChunkPosition representing the chunk this BlockPosition is in.
     *
     * @return a ChunkPosition
     */
    public ChunkPosition chunk() {
        return ChunkPosition.of(floorToBlock(this.x) >> 4, floorToBlock(this.z) >> 4);
    }

    /**
     * Returns a new EntityPosition with the coordinates of this BlockPosition.
     *
     * @return a new EntityPosition
     */
    public BlockPosition asBlock() {
        return BlockPosition.of(floorToBlock(this.x), floorToBlock(this.y), floorToBlock(this.z));
    }

    /**
     * Returns a new EntityPosition with the x coordinate set to the given value.
     *
     * @param x the x coordinate
     * @return a new EntityPosition
     */
    public EntityPosition withX(double x) {
        return new EntityPosition(x, this.y, this.z);
    }

    /**
     * Returns a new EntityPosition with the y coordinate set to the given value.
     *
     * @param y the y coordinate
     * @return a new EntityPosition
     */
    public EntityPosition withY(double y) {
        return new EntityPosition(this.x, y, this.z);
    }

    /**
     * Returns a new EntityPosition with the z coordinate set to the given value.
     *
     * @param z the z coordinate
     * @return a new EntityPosition
     */
    public EntityPosition withZ(double z) {
        return new EntityPosition(this.x, this.y, z);
    }

    /**
     * Adds the given values to the coordinates of this EntityPosition and returns a new EntityPosition.
     *
     * @param x the x coordinate to add
     * @param y the y coordinate to add
     * @param z the z coordinate to add
     * @return a new EntityPosition
     */
    public EntityPosition add(double x, double y, double z) {
        return new EntityPosition(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Subtracts the given values from the coordinates of this EntityPosition and returns a new EntityPosition.
     *
     * @param x the x coordinate to subtract
     * @param y the y coordinate to subtract
     * @param z the z coordinate to subtract
     * @return a new EntityPosition
     */
    public EntityPosition subtract(double x, double y, double z) {
        return new EntityPosition(this.x - x, this.y - y, this.z - z);
    }

    /**
     * Multiplies the coordinates of this EntityPosition by the given values and returns a new EntityPosition.
     *
     * @param x the x coordinate to multiply
     * @param y the y coordinate to multiply
     * @param z the z coordinate to multiply
     * @return a new EntityPosition
     */
    public EntityPosition multiply(double x, double y, double z) {
        return new EntityPosition(this.x * x, this.y * y, this.z * z);
    }

    /**
     * Divides the coordinates of this EntityPosition by the given values and returns a new EntityPosition.
     *
     * @param x the x coordinate to divide
     * @param y the y coordinate to divide
     * @param z the z coordinate to divide
     * @return a new EntityPosition
     */
    public EntityPosition divide(double x, double y, double z) {
        return new EntityPosition(this.x / x, this.y / y, this.z / z);
    }

    /**
     * Calculates the squared distance between this EntityPosition and the given EntityPosition.
     *
     * @param other the other EntityPosition
     * @return the squared distance
     */
    public double distanceSquared(EntityPosition other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the distance between this EntityPosition and the given EntityPosition.
     *
     * @param other the other EntityPosition
     * @return the distance
     */
    public double distance(EntityPosition other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public int hashCode() {
        int hash = Double.hashCode(this.x);
        hash = 31 * hash + Double.hashCode(this.y);
        return 31 * hash + Double.hashCode(this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityPosition)) {
            return false;
        }
        EntityPosition other = (EntityPosition) obj;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public String toString() {
        return "EntityPosition{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    /**
     * Creates a new EntityPosition with the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return a new EntityPosition
     */
    public static EntityPosition of(double x, double y, double z) {
        return new EntityPosition(x, y, z);
    }

    /**
     * Creates a new EntityPosition from the given Location.
     *
     * @param location the location
     * @return a new EntityPosition
     */
    public static EntityPosition fromLocation(Location location) {
        return new EntityPosition(location.getX(), location.getY(), location.getZ());
    }

    private static int floorToBlock(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
}
