package org.bf2.srs.fleetmanager.execution.manager.impl;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class StopExecutionControlException extends ExecutionControlException {

    static StopExecutionControlException create() {
        return new StopExecutionControlException();
    }
}
