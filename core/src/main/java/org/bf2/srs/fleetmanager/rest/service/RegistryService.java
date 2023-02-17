package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.auth.NotAuthorizedException;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryListDto;
import org.bf2.srs.fleetmanager.rest.service.model.ServiceStatusDto;
import org.bf2.srs.fleetmanager.rest.service.model.UsageStatisticsDto;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.common.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.common.TooManyInstancesException;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;

public interface RegistryService {

    RegistryDto createRegistry(RegistryCreateDto registry) throws RegistryStorageConflictException,
            TermsRequiredException, ResourceLimitReachedException, EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException, TooManyInstancesException, AccountManagementServiceException;

    RegistryListDto getRegistries(Integer page, Integer size, String orderBy, String search);

    RegistryDto getRegistry(String registryId) throws RegistryNotFoundException, NotAuthorizedException;

    void deleteRegistry(String registryId) throws RegistryNotFoundException, RegistryStorageConflictException, NotAuthorizedException;

    ServiceStatusDto getServiceStatus();

    /**
     * This method call may be somewhat expensive.
     * Do not use it for metrics directly/without caching.
     */
    UsageStatisticsDto getUsageStatistics();
}
