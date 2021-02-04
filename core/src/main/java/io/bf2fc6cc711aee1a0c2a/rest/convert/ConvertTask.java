package io.bf2fc6cc711aee1a0c2a.rest.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Task;
import io.bf2fc6cc711aee1a0c2a.rest.model.TaskRest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertTask {

    @Inject
    ObjectMapper mapper;

    @Inject
    ConvertTaskSchedule convertTaskSchedule;

    public TaskRest convert(@Valid Task task) {
        requireNonNull(task);
        try {
            return TaskRest.builder()
                    .id(task.getId())
                    .type(task.getType())
                    .data(mapper.writeValueAsString(task))
                    .schedule(convertTaskSchedule.convert(task.getSchedule()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert Task to JSON.");
        }
    }
}
