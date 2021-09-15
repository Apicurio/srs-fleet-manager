package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryListDto;
import org.bf2.srs.fleetmanager.rest.service.model.ServiceStatusDto;
import org.bf2.srs.fleetmanager.spi.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.TooManyInstancesException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;

public interface RegistryService {

    RegistryDto createRegistry(RegistryCreateDto registry) throws RegistryStorageConflictException,
            TermsRequiredException, ResourceLimitReachedException, EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException, TooManyInstancesException;

    RegistryListDto getRegistries(Integer page, Integer size, String orderBy, String search);

    RegistryDto getRegistry(String registryId) throws RegistryNotFoundException;

    void deleteRegistry(String registryId) throws RegistryNotFoundException, RegistryStorageConflictException;

    ServiceStatusDto getServiceStatus();
}
