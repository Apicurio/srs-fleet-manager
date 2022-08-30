package org.bf2.srs.fleetmanager.service.deployment;

import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@ApplicationScoped
public class DeploymentLoader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.deployments.allow-local-defaults")
    boolean allowLocalDeploymentLoader;

    @Inject
    LocalDeploymentProvider localProvider;

    @Inject
    ConfigDeploymentProvider configProvider;

    @Inject
    RegistryDeploymentService deploymentService;

    private boolean isRESTDeploymentManagementEnabled;

    public void run() {
        List<RegistryDeploymentCreate> newDeployments = Collections.emptyList();
        var fromConfig = configProvider.getDeploymentsToLoad();
        if (fromConfig.isEmpty()) {
            if (allowLocalDeploymentLoader) {
                newDeployments = localProvider.getDeploymentsToLoad();
            }
            isRESTDeploymentManagementEnabled = true;
        } else {
            newDeployments = fromConfig;
        }
        load(newDeployments);
    }

    private void load(List<RegistryDeploymentCreate> newDeployments) {
        checkDuplicates(newDeployments);
        Map<String, RegistryDeployment> currentDeployments = deploymentService.getRegistryDeployments().stream()
                .collect(Collectors.toMap(RegistryDeployment::getName, d -> d));

        for (RegistryDeploymentCreate dep : newDeployments) {
            try {
                RegistryDeployment deploymentData = currentDeployments.get(dep.getName());
                if (deploymentData == null) {
                    //deployment is new
                    deploymentService.createRegistryDeployment(dep);
                } else {
                    deploymentService.updateRegistryDeployment(dep);
                }
            } catch (RegistryDeploymentStorageConflictException ex) {
                log.error("Could not load Deployment " + dep, ex);
            }
        }
    }

    private void checkDuplicates(List<RegistryDeploymentCreate> newDeployments) {
        Set<String> names = new HashSet<>();
        List<String> duplicatedNames = newDeployments.stream()
                .map(RegistryDeploymentCreate::getName)
                .filter(name -> !names.add(name))
                .collect(Collectors.toList());
        if (!duplicatedNames.isEmpty()) {
            throw new IllegalArgumentException("Error in deployment loading, duplicated deployment names: " + duplicatedNames.toString());
        }
    }

    public boolean isRESTDeploymentManagementEnabled() {
        return isRESTDeploymentManagementEnabled;
    }
}
