package org.bf2.srs.fleetmanager.service.deployment;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentsConfigList;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

/**
 * Load initial deployments from a configuration file.
 *
 * @author Jakub Senko <m@jsenko.net>
 */
@ApplicationScoped
public class ConfigDeploymentProvider implements DeploymentProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "registry.deployments.config.file")
    Optional<File> deploymentsConfigFile;

    @Override
    public List<RegistryDeploymentCreate> getDeploymentsToLoad() {

        if (deploymentsConfigFile.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("Loading registry deployments config file from {}", deploymentsConfigFile.get().getAbsolutePath());

        YAMLMapper mapper = new YAMLMapper();

        RegistryDeploymentsConfigList deploymentsConfigList = null;
        try {
            deploymentsConfigList = mapper.readValue(deploymentsConfigFile.get(), RegistryDeploymentsConfigList.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Deployment configs file is in a wrong format", e);
        }

        return deploymentsConfigList.getDeployments();
    }
}
