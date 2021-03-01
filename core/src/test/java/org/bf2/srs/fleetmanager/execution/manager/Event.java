package org.bf2.srs.fleetmanager.execution.manager;

public enum Event {

    SUCCESS,
    STOP,
    FINALLY_EXECUTE_SUCCESS,
    EXCEPTION,
    RETRY,
    FORCE_RETRY;
}
