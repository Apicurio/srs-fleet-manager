package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface RegistryDeploymentService {

    void init() throws StorageConflictException, IOException;

    List<RegistryDeployment> getRegistryDeployments();

    RegistryDeployment createRegistryDeployment(@Valid RegistryDeploymentCreate data) throws StorageConflictException;

    RegistryDeployment getRegistryDeployment(Long registryDeploymentId) throws RegistryDeploymentNotFoundException;

    void deleteRegistryDeployment(Long registryDeploymentId) throws RegistryDeploymentNotFoundException, StorageConflictException;
}
