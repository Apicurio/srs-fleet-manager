package io.bf2fc6cc711aee1a0c2a.execution.impl.tasks;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Task;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskSchedule;
import io.bf2fc6cc711aee1a0c2a.execution.manager.WorkerContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@ToString
public class TestTask implements Task {

    @Getter
    private String id;

    @Getter
    private String type;

    @Getter
    private TaskSchedule schedule;

    @Getter
    private Deque<Command> commands = new ArrayDeque<>();

    @Getter
    private Command finalCommand = null;

    @Getter
    private int counter = 0;

    @Builder
    public TestTask(String id, TaskSchedule schedule) {
        if (id == null)
            id = UUID.randomUUID().toString();
        this.id = id;
        this.type = "TEST_T";

        if (schedule == null)
            schedule = TaskSchedule.builder().build();
        this.schedule = schedule;
    }

    public TestTask andThen(Command command) {
        commands.addLast(command);
        return this;
    }

    public TestTask finallyExecute(Command command) {
        finalCommand = command;
        return this;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "$class")
    public static interface Command {

        void execute(WorkerContext ctx, Task aTask);

        boolean done();
    }

    @NoArgsConstructor
    @Getter
    public static class RetryCommand implements Command {

        private int times;
        private int minRetries;
        private boolean force;

        @Builder
        public RetryCommand(int minRetries, boolean force) {
            this.minRetries = minRetries;
            this.force = force;
        }

        @Override
        public void execute(WorkerContext ctx, Task aTask) {
            times++;
            if (force)
                ctx.forceRetry();
            else
                ctx.retry(minRetries);
        }

        @Override
        public boolean done() {
            return times > 0;
        }
    }

    @Getter
    public static class StopCommand implements Command {

        private int times;

        @Builder
        public StopCommand() {
        }

        @Override
        public void execute(WorkerContext ctx, Task aTask) {
            times++;
            ctx.stop();
        }

        @Override
        public boolean done() {
            return times > 0;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BasicCommand implements Command {

        private int times;

        private boolean delayIncrement;
        private boolean delayThrowNPE;
        private boolean increment;
        private boolean throwNPE;

        @Builder
        public BasicCommand(boolean delayIncrement, boolean delayThrowNPE, boolean increment, boolean throwNPE) {
            this.delayIncrement = delayIncrement;
            this.delayThrowNPE = delayThrowNPE;

            this.increment = increment;
            this.throwNPE = throwNPE;
        }

        @Override
        public void execute(WorkerContext ctx, Task aTask) {
            TestTask task = (TestTask) aTask;
            times++;
            if (delayIncrement)
                ctx.delay(() -> task.counter++);
            if (delayThrowNPE)
                ctx.delay(() -> {
                    throw new NullPointerException();
                });
            if (increment)
                task.counter++;
            if (throwNPE)
                throw new NullPointerException();
        }

        @Override
        public boolean done() {
            return times > 0;
        }
    }
}
