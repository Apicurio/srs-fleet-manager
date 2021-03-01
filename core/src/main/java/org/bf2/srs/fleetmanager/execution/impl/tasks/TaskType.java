package org.bf2.srs.fleetmanager.execution.impl.tasks;

/**
 * WARNING: Changing enum names may cause corruption if persisted tasks are loaded.
 * (TODO Refactor?)
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public enum TaskType {

    SCHEDULE_REGISTRY_T,
    PROVISION_REGISTRY_TENANT_T,
    REGISTRY_HEARTBEAT_T,

    REGISTRY_DEPLOYMENT_HEARTBEAT_T,
}
