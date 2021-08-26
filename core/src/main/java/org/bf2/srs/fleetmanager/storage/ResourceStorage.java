package org.bf2.srs.fleetmanager.storage;

import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
// TODO: Unify Exception vs. Optional?
public interface ResourceStorage {

    //*** Registry

    boolean createOrUpdateRegistry(@Valid RegistryData registry) throws RegistryStorageConflictException;

    Optional<RegistryData> getRegistryById(@NotNull Long id);

    List<RegistryData> getAllRegistries();

    void deleteRegistry(@NotNull Long id) throws RegistryNotFoundException, RegistryStorageConflictException;

    //*** RegistryDeployment

    boolean createOrUpdateRegistryDeployment(@Valid RegistryDeploymentData rd) throws RegistryDeploymentStorageConflictException;

    List<RegistryDeploymentData> getAllRegistryDeployments();

    Optional<RegistryDeploymentData> getRegistryDeploymentById(@NotNull Long id);

    void deleteRegistryDeployment(@NotNull Long id) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException;
}
