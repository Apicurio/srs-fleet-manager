package io.bf2fc6cc711aee1a0c2a.execution.manager.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;
import lombok.SneakyThrows;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class QuartzTaskManager implements TaskManager {

    @Inject
    Scheduler quartzScheduler;

    @Inject
    ObjectMapper mapper;

    @SneakyThrows
    @Override
    public void start() {
        quartzScheduler.start();
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void submit(Task task) {

        String taskSerialized = mapper.writeValueAsString(task);

        JobDetail job = JobBuilder.newJob(JobWrapper.class)
                .withIdentity(task.getId(), "taskType-" + task.getTaskType().name())
                .usingJobData("task", taskSerialized)
                .build();

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(task.getId(), "taskType-" + task.getTaskType().name());

        // ===
        Duration interval = task.getTaskSchedule().getInterval();

        if (interval != null) {
            if (interval.toSeconds() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("TODO");
            }

            triggerBuilder = triggerBuilder.withSchedule(
                    (ScheduleBuilder<Trigger>) ((ScheduleBuilder<?>) CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                            .withInterval((int) interval.toSeconds(), DateBuilder.IntervalUnit.SECOND))
            );
        }

        // ===
        Instant firstExecuteAt = task.getTaskSchedule().getFirstExecuteAt();
        triggerBuilder.startAt(Date.from(firstExecuteAt));

        Trigger trigger = triggerBuilder.build();

        quartzScheduler.scheduleJob(job, trigger);
    }

    @Override
    @SneakyThrows
    public Set<Task> getAllTasks() {
        return quartzScheduler.getJobKeys(GroupMatcher.groupContains("taskType-")).stream()
                .map(this::getJobDetail)
                .map(j -> (String) j.getJobDataMap().get("task"))
                .map(this::deserialize)
                .collect(toSet());
    }

    @SneakyThrows
    private JobDetail getJobDetail(JobKey k) {
        return quartzScheduler.getJobDetail(k);
    }

    @SneakyThrows
    private Task deserialize(String s) {
        return mapper.readValue(s, Task.class);
    }

    @SneakyThrows
    private void unschedule(TriggerKey k) {
        quartzScheduler.unscheduleJob(k);
    }

    @Override
    @SneakyThrows
    public Set<Task> getTasksByType(TaskType taskType) {
        return quartzScheduler.getJobKeys(GroupMatcher.groupEquals("taskType-" + taskType.name())).stream()
                .map(this::getJobDetail)
                .map(j -> (String) j.getJobDataMap().get("task"))
                .map(this::deserialize)
                .collect(toSet());
    }

    @Override
    public Optional<Task> getTaskById(String taskId) {
        return getAllTasks().stream()
                .filter(t -> taskId.equals(t.getId()))
                .findFirst();
    }

    @Override
    @SneakyThrows
    public void remove(Task task) {
        quartzScheduler.getTriggerKeys(GroupMatcher.groupEquals("taskType-" + task.getTaskType().name())).stream()
                .forEach(this::unschedule);
    }

    @SneakyThrows
    @Override
    public void stop() {
        quartzScheduler.shutdown(true);
    }
}
