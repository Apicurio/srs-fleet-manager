package org.bf2.srs.fleetmanager.common.storage;

import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.common.storage.util.QueryConfig;
import org.bf2.srs.fleetmanager.common.storage.util.QueryResult;
import org.bf2.srs.fleetmanager.common.storage.util.SearchQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
// TODO: Unify Exception vs. Optional?
public interface ResourceStorage {

    //*** Registry

    boolean createOrUpdateRegistry(@Valid RegistryData registry) throws RegistryStorageConflictException;

    Optional<RegistryData> getRegistryById(@NotEmpty String id);

    List<RegistryData> getAllRegistries();

    List<RegistryData> getRegistriesByOwner(@NotEmpty String owner);

    void deleteRegistry(@NotEmpty String id) throws RegistryNotFoundException, RegistryStorageConflictException;

    //*** RegistryDeployment

    boolean createOrUpdateRegistryDeployment(@Valid RegistryDeploymentData rd) throws RegistryDeploymentStorageConflictException, RegistryDeploymentNotFoundException;

    List<RegistryDeploymentData> getAllRegistryDeployments();

    Optional<RegistryDeploymentData> getRegistryDeploymentById(@NotNull Long id);

    void deleteRegistryDeployment(@NotNull Long id) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException;

    QueryResult<RegistryData> executeRegistrySearchQuery(@NotNull SearchQuery query, @Valid QueryConfig config);

    /**
     * Queries the DB to get the total # of Registry instances.
     */
    long getRegistryCountTotal();

    /**
     * Queries the DB to get the total # of Registry instances per each status value.
     */
    Map<String, Long> getRegistryCountPerStatus();

    long getRegistryCountPerOrgId(@NotEmpty String orgId);

    /**
     * Queries the DB to get the total # of Registry instances per each instance type value.
     */
    Map<String, Long> getRegistryCountPerType();

    long getRegistryOwnerCount();

    long getRegistryOrganisationCount();
}
