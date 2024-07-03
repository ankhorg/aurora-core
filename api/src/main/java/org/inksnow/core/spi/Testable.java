package org.inksnow.core.spi;

/**
 * A testable object, if a service provider implements this interface,
 * AuroraCore will not be loaded if the {@link #isAvailable()} method returns false.
 *
 * <p> For example:
 *
 * <pre>
 *   public static class Factory implements ItemProvider.Factory, Testable {
 *   ...
 *     public boolean isAvailable() {
 *       try {
 *         Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
 *         return true;
 *       } catch (ClassNotFoundException e) {
 *         return false;
 *       }
 *     }
 *     ...
 *   }
 * </pre>
 */
public interface Testable {
    /**
     * Test if the object is available.
     *
     * @return true if the object is available, otherwise false.
     */
    boolean isAvailable();
}
