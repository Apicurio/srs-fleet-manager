package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.REGISTRY_HEARTBEAT;
import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.SCHEDULE_REGISTRY;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class RegistryHeartbeatTask extends AbstractTask {

    private long registryId;

    @Builder
    public RegistryHeartbeatTask(long registryId) {
        super(REGISTRY_HEARTBEAT);
        this.registryId = registryId;
        this.taskSchedule = TaskSchedule.builder().interval(Duration.ofSeconds(10)).build();
    }
}
