package io.bf2fc6cc711aee1a0c2a.rest.model;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TaskRest {

    private String id;

    private TaskType type;

    private String data;
}
