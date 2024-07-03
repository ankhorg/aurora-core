package org.inksnow.core.spi;

/**
 * Represents the priority of a service.
 * The priority of a service is used to determine the order of services in a service registry.
 */
public enum ServicePriority {
    /**
     * The lowest priority.
     */
    LOWEST,
    /**
     * A low priority.
     */
    LOW,
    /**
     * A normal priority.
     */
    NORMAL,
    /**
     * A high priority.
     */
    HIGH,
    /**
     * The highest priority.
     */
    HIGHEST,
    /**
     * The priority must be used.
     * <p>
     * If more than one service has this priority, it will cause an error.
     *
     * @implNote should throw {@link IllegalStateException} if more than one service has this priority
     */
    MUST;
}
