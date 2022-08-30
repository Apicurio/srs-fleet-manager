package org.bf2.srs.fleetmanager.rest.service.impl;

import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static java.util.stream.Collectors.toList;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_DEPLOYMENT_ID;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryDeploymentServiceImpl implements RegistryDeploymentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistryDeployment convertRegistryDeployment;

    @Inject
    TenantManagerService tms;

    @Override
    @Audited
    public RegistryDeployment createRegistryDeployment(RegistryDeploymentCreate deploymentCreate) throws RegistryDeploymentStorageConflictException {
        // NOTE: Deployments cannot be updated, unless a safe way to migrate the tenants
        // is implemented. - unless te URLs actually changed - any way to check this?

        // Check if the deployment with the given name already exists,
        // so we do not perform an inadvertent update.
        if (storage.getRegistryDeploymentByName(deploymentCreate.getName()).isPresent()) {
            throw new RegistryDeploymentStorageConflictException();
        }
        RegistryDeploymentData deployment = convertRegistryDeployment.convert(deploymentCreate);
        try {
            deployment.getStatus().setValue(RegistryDeploymentStatusValue.AVAILABLE.value());
            storage.createOrUpdateRegistryDeployment(deployment);
            // TODO The "RegistryDeploymentHeartbeatTask" task is currently not used.
        } catch (RegistryDeploymentNotFoundException e) {
            // This error should not be possible
            log.error("Unexpected error", e);
            throw new RegistryDeploymentStorageConflictException();
        }
        return convertRegistryDeployment.convert(deployment);
    }

    @Override
    @Audited
    public RegistryDeployment updateRegistryDeployment(RegistryDeploymentCreate deploymentCreate) throws RegistryDeploymentStorageConflictException {
        // NOTES:
        // 1. Deployment is currently only updatable by name, not ID
        // 2. Deployments cannot be safely updated, unless:
        //   - A safe way to migrate the tenants is implemented (not available at the moment)
        //   - There are no tenants
        //   - The URLs actually changed (TODO: a safety check is needed)

        // Check if the deployment with the given name already exists,
        // so we do not perform an inadvertent create.
        var existingOpt = storage.getRegistryDeploymentByName(deploymentCreate.getName());
        if (existingOpt.isEmpty()) {
            throw new RegistryDeploymentStorageConflictException();
        }
        var existing = existingOpt.get();
        // TODO: Convert?
        existing.setTenantManagerUrl(deploymentCreate.getTenantManagerUrl());
        existing.setRegistryDeploymentUrl(deploymentCreate.getRegistryDeploymentUrl());
        try {
            storage.createOrUpdateRegistryDeployment(existing);
            // TODO Consider the "RegistryDeploymentHeartbeatTask" task if used again
        } catch (RegistryDeploymentNotFoundException e) {
            // This error should not be possible
            log.error("Unexpected error", e);
            throw new RegistryDeploymentStorageConflictException();
        }
        return convertRegistryDeployment.convert(existing);
    }

    @Override
    @Audited
    public List<RegistryDeployment> getRegistryDeployments() {
        return storage.getAllRegistryDeployments().stream()
                .map(convertRegistryDeployment::convert)
                .collect(toList());
    }

    @Override
    @Audited(extractParameters = {"0", KEY_DEPLOYMENT_ID})
    public RegistryDeployment getRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        return storage.getRegistryDeploymentById(id)
                .map(convertRegistryDeployment::convert)
                .orElseThrow(() -> new RegistryDeploymentNotFoundException());
    }

    @Override
    @Audited(extractParameters = {"0", KEY_DEPLOYMENT_ID})
    @Transactional // To ensure the count does not change under us
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException {
        // Prevent deployment from being deleted if there are still any instances
        if (storage.getRegistryCountPerDeploymentId(id) > 0) {
            throw new RegistryDeploymentStorageConflictException();
        }
        storage.deleteRegistryDeployment(id);
    }
}
