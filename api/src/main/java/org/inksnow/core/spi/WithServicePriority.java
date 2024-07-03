package org.inksnow.core.spi;

/**
 * Represents the priority of a service.
 *
 * @see ServicePriority
 */
public interface WithServicePriority {
    /**
     * Returns the priority of this service.
     *
     * @return the priority of this service
     */
    ServicePriority priority();
}
