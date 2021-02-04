package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskSchedule;
import io.bf2fc6cc711aee1a0c2a.rest.model.TaskScheduleRest;

import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertTaskSchedule {

    @Inject
    ConvertISO8601 convertISO8601;

    public TaskScheduleRest convert(@Valid TaskSchedule schedule) {
        requireNonNull(schedule);
        return TaskScheduleRest.builder()
                .firstExecuteAt(convertISO8601.convert(schedule.getFirstExecuteAt()))
                .priority(schedule.getPriority())
                .intervalSec(ofNullable(schedule.getInterval()).map(Duration::getSeconds).orElse(null))
                .build();
    }
}
