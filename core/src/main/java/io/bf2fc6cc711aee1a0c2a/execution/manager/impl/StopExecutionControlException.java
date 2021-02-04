package io.bf2fc6cc711aee1a0c2a.execution.manager.impl;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class StopExecutionControlException extends ExecutionControlException {

    static StopExecutionControlException create() {
        return new StopExecutionControlException();
    }
}
