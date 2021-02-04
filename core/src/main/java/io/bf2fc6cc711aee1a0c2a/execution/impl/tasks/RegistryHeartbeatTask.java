package io.bf2fc6cc711aee1a0c2a.execution.impl.tasks;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TaskType.REGISTRY_HEARTBEAT_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class RegistryHeartbeatTask extends AbstractTask {

    private long registryId;

    @Builder
    public RegistryHeartbeatTask(long registryId) {
        super(REGISTRY_HEARTBEAT_T);
        this.registryId = registryId;
        this.schedule = TaskSchedule.builder().interval(Duration.ofSeconds(10)).build();
    }
}
