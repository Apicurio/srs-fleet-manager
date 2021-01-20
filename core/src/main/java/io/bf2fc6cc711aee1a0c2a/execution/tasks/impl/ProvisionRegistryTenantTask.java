package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.PROVISION_REGISTRY_TENANT;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ProvisionRegistryTenantTask extends AbstractTask {

    private long registryId;

    @Builder
    public ProvisionRegistryTenantTask(long registryId) {
        super(PROVISION_REGISTRY_TENANT);
        this.registryId = registryId;
    }
}
