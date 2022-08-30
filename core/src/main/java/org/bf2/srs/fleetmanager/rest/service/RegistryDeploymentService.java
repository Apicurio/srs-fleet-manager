package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;

import java.util.List;
import javax.validation.Valid;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface RegistryDeploymentService {

    RegistryDeployment createRegistryDeployment(@Valid RegistryDeploymentCreate data) throws RegistryDeploymentStorageConflictException;

    RegistryDeployment getRegistryDeployment(Long registryDeploymentId) throws RegistryDeploymentNotFoundException;

    List<RegistryDeployment> getRegistryDeployments();

    RegistryDeployment updateRegistryDeployment(@Valid RegistryDeploymentCreate deploymentCreate) throws RegistryDeploymentStorageConflictException;

    void deleteRegistryDeployment(Long registryDeploymentId) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException;
}
