package org.bf2.srs.fleetmanager.execution.impl.workers;

/**
 * WARNING: Changing enum names may cause corruption if persisted tasks are loaded.
 * (TODO Refactor?)
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public enum WorkerType {

    SCHEDULE_REGISTRY_W,
    PROVISION_REGISTRY_TENANT_W,
    REGISTRY_HEARTBEAT_W,

    REGISTRY_DEPLOYMENT_HEARTBEAT_W,
}
