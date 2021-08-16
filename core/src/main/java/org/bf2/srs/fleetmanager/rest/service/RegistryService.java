package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryList;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

public interface RegistryService {

    Registry createRegistry(RegistryCreate registry) throws StorageConflictException, TermsRequiredException, ResourceLimitReachedException;

    RegistryList getRegistries(Integer page, Integer size, String orderBy, String search);

    Registry getRegistry(String registryId) throws RegistryNotFoundException;

    void deleteRegistry(String registryId) throws RegistryNotFoundException, StorageConflictException;
}
