package org.bf2.srs.fleetmanager.spi.tenants.impl;

import java.time.Duration;
import java.time.Instant;

public class WrappedValue<V> {

    private final Duration lifetime;
    private final Instant lastUpdate;
    private final V value;

    public WrappedValue(Duration lifetime, Instant lastUpdate, V value) {
        this.lifetime = lifetime;
        this.lastUpdate = lastUpdate;
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        return lastUpdate.plus(lifetime).isBefore(Instant.now());
    }
}