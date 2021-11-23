package org.bf2.srs.fleetmanager.storage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.util.SearchQuery;

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

    PanacheQuery<RegistryData> executeRegistrySearchQuery(SearchQuery query, Sort sort);

    /**
     * Queries the DB to get the total # of Registry instances.
     */
    long getRegistryCountTotal();

    /**
     * Queries the DB to get the total # of Registry instances per each status value.
     */
    Map<String, Long> getRegistryCountPerStatus();

    /**
     * Queries the DB to get the total # of Registry instances per each instance type value.
     */
    Map<String, Long> getRegistryCountPerType();

    long getRegistryOwnerCount();

    long getRegistryOrganisationCount();
}
