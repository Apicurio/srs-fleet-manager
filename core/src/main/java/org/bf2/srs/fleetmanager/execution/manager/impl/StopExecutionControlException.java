package org.bf2.srs.fleetmanager.execution.manager.impl;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class StopExecutionControlException extends ExecutionControlException {

    private static final long serialVersionUID = 5688640910399993346L;

    static StopExecutionControlException create() {
        return new StopExecutionControlException();
    }
}
