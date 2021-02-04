package io.bf2fc6cc711aee1a0c2a.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TaskType.SCHEDULE_REGISTRY_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ScheduleRegistryTask extends AbstractTask {

    private long registryId;

    @Builder
    public ScheduleRegistryTask(long registryId) {
        super(SCHEDULE_REGISTRY_T);
        this.registryId = registryId;
    }
}
