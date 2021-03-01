package org.bf2.srs.fleetmanager.rest.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.rest.model.TaskRest;

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
