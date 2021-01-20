package io.bf2fc6cc711aee1a0c2a.execution.tasks;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "$class")
public interface Task {

    String getId();

    TaskType getTaskType();

    TaskSchedule getTaskSchedule();
}
