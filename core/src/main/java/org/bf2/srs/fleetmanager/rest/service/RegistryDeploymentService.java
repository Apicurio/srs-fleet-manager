package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface RegistryDeploymentService {

    List<RegistryDeployment> getRegistryDeployments();

    RegistryDeployment createRegistryDeployment(@Valid RegistryDeploymentCreate data) throws StorageConflictException;

    RegistryDeployment getRegistryDeployment(Long registryDeploymentId) throws RegistryDeploymentNotFoundException;

    void deleteRegistryDeployment(Long registryDeploymentId) throws RegistryDeploymentNotFoundException, StorageConflictException;
}
