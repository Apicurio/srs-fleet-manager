package org.bf2.srs.fleetmanager.storage;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
// TODO: Unify Exception vs. Optional?
public interface ResourceStorage {

    //*** Registry

    boolean createOrUpdateRegistry(@Valid RegistryData registry) throws RegistryStorageConflictException;

    Optional<RegistryData> getRegistryById(@NotNull String id);

    List<RegistryData> getAllRegistries();

    List<RegistryData> getRegistriesByOwner(String owner);

    void deleteRegistry(@NotNull String id) throws RegistryNotFoundException, RegistryStorageConflictException;

    //*** RegistryDeployment

    boolean createOrUpdateRegistryDeployment(@Valid RegistryDeploymentData rd) throws RegistryDeploymentStorageConflictException, RegistryDeploymentNotFoundException;

    List<RegistryDeploymentData> getAllRegistryDeployments();

    Optional<RegistryDeploymentData> getRegistryDeploymentById(@NotNull Long id);

    void deleteRegistryDeployment(@NotNull Long id) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException;

}
