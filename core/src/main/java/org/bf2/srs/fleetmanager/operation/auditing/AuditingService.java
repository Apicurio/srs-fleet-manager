package org.bf2.srs.fleetmanager.operation.auditing;

/**
 * Provide a way to manually record auditing events.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface AuditingService {

    default void addTraceMetadata(String key, Object value) {
        addTraceMetadata(key, value, false);
    }

    void addTraceMetadata(String key, Object value, boolean overwrite);

    void recordEvent(AuditingEvent event);
}
