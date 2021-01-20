package io.bf2fc6cc711aee1a0c2a.rest.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.rest.model.TaskRest;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConvertTask {

    @Inject
    ObjectMapper mapper;

    @SneakyThrows
    public TaskRest convert(Task task) {
        return TaskRest.builder()
                .id(task.getId())
                .type(task.getTaskType())
                .data(mapper.writeValueAsString(task))
                .build();
    }
}
