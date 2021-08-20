package org.bf2.srs.fleetmanager.rest.service.impl;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentsConfigList;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.ForbiddenException;

import static java.util.stream.Collectors.toList;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryDeploymentServiceImpl implements RegistryDeploymentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    Validator validator;

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistryDeployment convertRegistryDeployment;

    @ConfigProperty(name = "registry.deployments.config.file")
    Optional<File> deploymentsConfigFile;

    @Override
    public void init() throws StorageConflictException, IOException {

        if (deploymentsConfigFile.isEmpty()) {
            return;
        }

        log.info("Loading registry deployments config file from {}", deploymentsConfigFile.get().getAbsolutePath());

        YAMLMapper mapper = new YAMLMapper();

        RegistryDeploymentsConfigList deploymentsConfigList = mapper.readValue(deploymentsConfigFile.get(), RegistryDeploymentsConfigList.class);

        List<RegistryDeploymentCreate> staticDeployments = deploymentsConfigList.getDeployments();

        Set<String> names = new HashSet<>();
        List<String> duplicatedNames = staticDeployments.stream()
                .map(d-> {
                    Set<ConstraintViolation<RegistryDeploymentCreate>> errors = validator.validate(d);
                    if (!errors.isEmpty()) {
                        throw new ConstraintViolationException(errors);
                    }
                    return d;
                })
                .filter(d -> !names.add(d.getName()))
                .map(d -> d.getName())
                .collect(Collectors.toList());
        if (!duplicatedNames.isEmpty()) {
            throw new IllegalArgumentException("Error in static deployments config, duplicated deployments name: " + duplicatedNames.toString());
        }

        Map<String, RegistryDeploymentData> currentDeployments = storage.getAllRegistryDeployments().stream()
                .collect(Collectors.toMap(d -> d.getName(), d -> d));

        for (RegistryDeploymentCreate dep : staticDeployments) {

            RegistryDeploymentData deploymentData = currentDeployments.get(dep.getName());

            if (deploymentData == null) {
                //deployment is new
                deploymentData = convertRegistryDeployment.convert(dep);
            } else {
                RegistryDeploymentData configured = convertRegistryDeployment.convert(dep);
                if (deploymentData.getRegistryDeploymentUrl().equals(configured.getRegistryDeploymentUrl())
                        && deploymentData.getTenantManagerUrl().equals(configured.getTenantManagerUrl())) {
                    //no changes in the deployment
                    continue;
                }

                deploymentData.setRegistryDeploymentUrl(dep.getRegistryDeploymentUrl());
                deploymentData.setTenantManagerUrl(dep.getTenantManagerUrl());
            }

            createOrUpdateRegistryDeployment(deploymentData);
        }
    }

    @Override
    public RegistryDeployment createRegistryDeployment(@Valid RegistryDeploymentCreate deploymentCreate) throws StorageConflictException {
        if (deploymentsConfigFile.isPresent()) {
            throw new ForbiddenException();
        }
        RegistryDeploymentData deployment = convertRegistryDeployment.convert(deploymentCreate);
        createOrUpdateRegistryDeployment(deployment);
        return convertRegistryDeployment.convert(deployment);
    }

    private void createOrUpdateRegistryDeployment(RegistryDeploymentData deployment) throws StorageConflictException {
        deployment.getStatus().setValue(RegistryDeploymentStatusValue.AVAILABLE.value());
        storage.createOrUpdateRegistryDeployment(deployment);
        // TODO This task is (temporarily) not used. Enable when needed.
        //if (created) {
        //    tasks.submit(RegistryDeploymentHeartbeatTask.builder().deploymentId(deployment.getId()).build());
        //}
    }

    @Override
    public List<RegistryDeployment> getRegistryDeployments() {
        return storage.getAllRegistryDeployments().stream()
                .map(convertRegistryDeployment::convert)
                .collect(toList());
    }

    @Override
    public RegistryDeployment getRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        return storage.getRegistryDeploymentById(id)
                .map(convertRegistryDeployment::convert)
                .orElseThrow(() -> RegistryDeploymentNotFoundException.create(id));
    }

    @Override
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException, StorageConflictException {
        if (deploymentsConfigFile.isPresent()) {
            throw new ForbiddenException();
        }
        storage.deleteRegistryDeployment(id);
    }
}
