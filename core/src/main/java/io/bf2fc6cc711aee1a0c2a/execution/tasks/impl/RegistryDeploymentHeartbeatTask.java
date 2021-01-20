package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.REGISTRY_DEPLOYMENT_HEARTBEAT;
import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.REGISTRY_HEARTBEAT;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class RegistryDeploymentHeartbeatTask extends AbstractTask {

    private long deploymentId;

    @Builder
    public RegistryDeploymentHeartbeatTask(long deploymentId) {
        super(REGISTRY_DEPLOYMENT_HEARTBEAT);
        this.deploymentId = deploymentId;
        this.taskSchedule = TaskSchedule.builder().interval(Duration.ofSeconds(10)).build();
    }
}
