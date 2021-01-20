package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.SCHEDULE_REGISTRY;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ScheduleRegistryTask extends AbstractTask {

    private long registryId;

    @Builder
    public ScheduleRegistryTask(long registryId) {
        super(SCHEDULE_REGISTRY);
        this.registryId = registryId;
    }
}
