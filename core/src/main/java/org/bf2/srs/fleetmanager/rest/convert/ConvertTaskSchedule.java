package org.bf2.srs.fleetmanager.rest.convert;

import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;
import org.bf2.srs.fleetmanager.rest.model.TaskScheduleRest;

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
