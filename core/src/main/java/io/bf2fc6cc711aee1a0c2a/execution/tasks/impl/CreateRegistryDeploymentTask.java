package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentRest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.CREATE_REGISTRY_DEPLOYMENT;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class CreateRegistryDeploymentTask extends AbstractTask {

    private RegistryDeploymentRest registryDeployment;

    @Builder
    public CreateRegistryDeploymentTask(RegistryDeploymentRest registryDeployment) {
        super(CREATE_REGISTRY_DEPLOYMENT);
        this.registryDeployment = registryDeployment;
    }
}
