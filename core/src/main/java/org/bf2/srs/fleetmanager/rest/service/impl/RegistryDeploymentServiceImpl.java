package org.bf2.srs.fleetmanager.rest.service.impl;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryDeploymentServiceImpl implements RegistryDeploymentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistryDeployment convertRegistryDeployment;

    @Inject
    AccountManagementService accountManagementService;

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @Override
    public RegistryDeployment createRegistryDeployment(@Valid RegistryDeploymentCreate deploymentCreate) throws StorageConflictException {
        boolean allowed = true;
        if (isResolvable(securityIdentity)) {
            //TODO fill resoure type and cluster id
            final AccountInfo accountInfo = authService.extractAccountInfo();
            allowed = accountManagementService.hasEntitlements(accountInfo, "", "");
        }
        if (allowed) {
            //TODO validate values
            //registryDeploymentURl finishes without / starts with http ...
            RegistryDeploymentData deployment = convertRegistryDeployment.convert(deploymentCreate);
            storage.createOrUpdateRegistryDeployment(deployment);
            tasks.submit(RegistryDeploymentHeartbeatTask.builder().deploymentId(deployment.getId()).build());
            return convertRegistryDeployment.convert(deployment);
        }
        throw new ForbiddenException();
    }

    @Override
    public List<RegistryDeployment> getRegistryDeployments() {
        return storage.getAllRegistryDeployments().stream()
                .map(convertRegistryDeployment::convert)
                .collect(toList());
    }

    @Override
    public RegistryDeployment getRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        return storage.getRegistryDeploymentById(id)
                .map(convertRegistryDeployment::convert)
                .orElseThrow(() -> RegistryDeploymentNotFoundException.create(id));
    }

    @Override
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException, StorageConflictException {
        boolean allowed = true;
        if (isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            allowed = accountInfo.isAdmin();
        }
        if (allowed) {
            storage.deleteRegistryDeployment(id);
        }
        throw new ForbiddenException();
    }
}
