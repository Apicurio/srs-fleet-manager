package org.bf2.srs.fleetmanager.execution.manager.impl;

import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.Worker;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import static java.util.Objects.requireNonNull;

/**
 * A single place for defining the format of Quartz identifiers.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class QuartzIDs {

    public static String TASK_GROUP_PREFIX = "taskType-";

    public static String idForTaskId(String taskId) {
        requireNonNull(taskId);
        return taskId;
    }

    public static String idForTask(Task task) {
        requireNonNull(task);
        return idForTaskId(task.getId());
    }

    public static String groupForTaskType(String taskType) {
        requireNonNull(taskType);
        return TASK_GROUP_PREFIX + taskType;
    }

    public static String groupForTask(Task task) {
        requireNonNull(task);
        return groupForTaskType(task.getType());
    }

    public static String jobDetailKeyForTask() {
        return "task";
    }

    public static String jobDetailKeyForWorker(Worker worker) {
        requireNonNull(worker);
        return "workerType-" + worker.getType();
    }

    public static JobKey jobKeyForTask(Task task) {
        requireNonNull(task);
        return JobKey.jobKey(idForTask(task), groupForTask(task));
    }

    public static TriggerKey triggerKeyForTask(Task task) {
        requireNonNull(task);
        return TriggerKey.triggerKey(idForTask(task), groupForTask(task));
    }
}
