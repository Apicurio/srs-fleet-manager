package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.TaskSchedule;

import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertTaskSchedule {

    @Inject
    ConvertISO8601 convertISO8601;

    public TaskSchedule convert(@Valid @NotNull org.bf2.srs.fleetmanager.execution.manager.TaskSchedule schedule) {
        return TaskSchedule.builder()
                .firstExecuteAt(convertISO8601.convert(schedule.getFirstExecuteAt()))
                .priority(schedule.getPriority())
                .intervalSec(ofNullable(schedule.getInterval()).map(Duration::getSeconds).orElse(null))
                .build();
    }
}
