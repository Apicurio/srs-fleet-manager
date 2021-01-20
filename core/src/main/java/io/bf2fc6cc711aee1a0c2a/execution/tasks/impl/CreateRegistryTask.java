package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryRest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class CreateRegistryTask extends AbstractTask {

    private RegistryRest registry;

    @Builder
    public CreateRegistryTask(RegistryRest registry) {
        super(TaskType.CREATE_REGISTRY);
        this.registry = registry;
    }
}
