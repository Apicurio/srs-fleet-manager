package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.rest.publicapi.ApiResource;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.util.SecurityUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.OWNER_ID_PLACEHOLDER;
import static org.bf2.srs.fleetmanager.util.SecurityUtil.OWNER_PLACEHOLDER;
import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ApiResourceImpl implements ApiResource {

    private static final String SCHEMA;
    private static final String SUBSCRIPTION_ID_PLACEHOLDER = "Subscription without auth";

    static {
        try {
            SCHEMA = new String(ApiResourceImpl.class.getResourceAsStream("/srs-fleet-manager.json").readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load Open API schema for the v1 REST interface.", e);
        }
    }

    @Inject
    RegistryService registryService;

    @Inject
    Convert convert;

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @Inject
    AccountManagementService accountManagementService;

    @ConfigProperty(name = "srs-fleet-manager.default-org")
    String defaultOrg;

    @ConfigProperty(name = "srs-fleet-manager.registry.product-id")
    String productId;

    @Override
    public RegistryListRest getRegistries(Integer page,
                                          Integer size,
                                          String orderBy, String search) {
        return convert.convert(registryService.getRegistries(page, size, orderBy, search));
    }

    @Override
    public RegistryRest createRegistry(RegistryCreateRest data) throws StorageConflictException {
        String owner = OWNER_PLACEHOLDER;
        String orgId = defaultOrg;
        Long ownerId = OWNER_ID_PLACEHOLDER;
        String subscriptionId = SUBSCRIPTION_ID_PLACEHOLDER;
        if (SecurityUtil.isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            owner = accountInfo.getAccountUsername();
            orgId = accountInfo.getOrganizationId();
            ownerId = accountInfo.getAccountId();
            subscriptionId = accountManagementService.createResource(accountInfo, "cluster.aws", "", productId);
        }
        return convert.convert(registryService.createRegistry(convert.convert(data, owner, orgId, ownerId, subscriptionId)));
    }

    @Override
    public RegistryRest getRegistry(String id) throws RegistryNotFoundException {
        return convert.convert(registryService.getRegistry(id));
    }

    @Override
    public void deleteRegistry(String id) throws StorageConflictException, RegistryNotFoundException {
        //First we get the subscriptionId for the given registry
        final String subscriptionId = registryService.getRegistry(id).getSubscriptionId();
        //Then we delete the registry instance
        registryService.deleteRegistry(id);
        //And finally, if no exceptions have been thrown by the delete registry operation, we return the subscription to the user
        accountManagementService.deleteSubscription(subscriptionId);
    }
}
