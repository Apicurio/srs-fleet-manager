package io.bf2fc6cc711aee1a0c2a.execution.impl.workers;

import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TestTask;
import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TestTask.Command;
import io.bf2fc6cc711aee1a0c2a.execution.manager.DataCollector;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Task;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.WorkerContext;
import io.bf2fc6cc711aee1a0c2a.execution.manager.impl.RetryExecutionControlException;
import io.bf2fc6cc711aee1a0c2a.execution.manager.impl.StopExecutionControlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class DataCollectingWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    DataCollector data;

    @Override
    public String getType() {
        return "TEST_W";
    }

    @Override
    public boolean supports(Task task) {
        return "TEST_T".equals(task.getType());
    }

    @Override
    public void execute(Task aTask, WorkerContext ctl) {

        boolean finished = false;
        try {
            TestTask task = (TestTask) aTask;
            Command command = task.getCommands().peekFirst();
            if (command != null && command.done()) {
                task.getCommands().removeFirst();
                command = task.getCommands().peekFirst();
            }
            if (command != null) {
                command.execute(ctl, task);
            } else {
                // Do not record stop caused by end of commands
                finished = true;
                ctl.stop();
            }
            data.recordSuccess();
        } catch (RetryExecutionControlException ex) {
            if (ex.isForce())
                data.recordForceRetry();
            else
                data.recordRetry();
            throw ex;
        } catch (StopExecutionControlException ex) {
            if (!finished)
                data.recordStop();
            throw ex;
        } catch (Exception ex) {
            data.recordException();
            throw ex;
        } finally {
            if(!finished)
                data.recordExecution();
        }
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        TestTask task = (TestTask) aTask;
        try {
            Command command = task.getFinalCommand();
            if (command != null)
                command.execute(ctl, task);
            data.recordFinallyExecuteSuccess();
        } finally {
            data.recordCounter(task.getCounter());
            data.recordFinallyExecuteAttempt();
            data.signalFinished();
        }
    }
}
