package org.bf2.srs.fleetmanager.ams.mock;

import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.common.storage.util.QueryConfig;
import org.bf2.srs.fleetmanager.common.storage.util.QueryResult;
import org.bf2.srs.fleetmanager.common.storage.util.SearchQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApplicationScoped
public class ResourceStorageNoopMock implements ResourceStorage {

    @Override
    public boolean createOrUpdateRegistry(@Valid RegistryData registry) throws RegistryStorageConflictException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<RegistryData> getRegistryById(@NotEmpty String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RegistryData> getAllRegistries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RegistryData> getRegistriesByOwner(@NotEmpty String owner) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteRegistry(@NotEmpty String id) throws RegistryNotFoundException, RegistryStorageConflictException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createOrUpdateRegistryDeployment(@Valid RegistryDeploymentData rd) throws RegistryDeploymentStorageConflictException, RegistryDeploymentNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RegistryDeploymentData> getAllRegistryDeployments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<RegistryDeploymentData> getRegistryDeploymentById(@NotNull Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteRegistryDeployment(@NotNull Long id) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryResult<RegistryData> executeRegistrySearchQuery(@NotNull SearchQuery query, @Valid QueryConfig config) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getRegistryCountTotal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Long> getRegistryCountPerStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getRegistryCountPerOrgId(@NotEmpty String orgId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Long> getRegistryCountPerType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getRegistryOwnerCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getRegistryOrganisationCount() {
        throw new UnsupportedOperationException();
    }
}
