package org.bf2.srs.fleetmanager.execution.manager;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.enterprise.context.ApplicationScoped;

import static org.bf2.srs.fleetmanager.execution.manager.Event.*;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Getter
@ApplicationScoped
public class DataCollector {

    private int executions = 0;
    private int finallyExecuteAttempts = 0;
    private int finallyExecuteSuccess = 0;
    private int counter;

    private List<Event> events = new ArrayList<>();

    private boolean finished = false;
    private final Lock lock = new ReentrantLock();
    private final Condition finishedCond = lock.newCondition();

    public void recordExecution() {
        executions++;
    }

    public void recordSuccess() {
        events.add(SUCCESS);
    }

    public void recordStop() {
        events.add(STOP);
    }

    public void recordFinallyExecuteAttempt() {
        finallyExecuteAttempts++;
    }

    public void recordException() {
        events.add(EXCEPTION);
    }

    public void recordRetry() {
        events.add(RETRY);
    }

    public void recordForceRetry() {
        events.add(FORCE_RETRY);
    }

    public void recordFinallyExecuteSuccess() {
        finallyExecuteSuccess++;
        events.add(FINALLY_EXECUTE_SUCCESS);
    }

    public void recordCounter(int counter) {
        this.counter = counter;
    }

    public void awaitFinished() {
        lock.lock();
        try {
            while (!finished)
                finishedCond.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }

    public void signalFinished() {
        lock.lock();
        try {
            finished = true;
            finishedCond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        executions = 0;
        finallyExecuteAttempts = 0;
        finallyExecuteSuccess = 0;
        events = new ArrayList<>();

        finished = false;
    }
}
