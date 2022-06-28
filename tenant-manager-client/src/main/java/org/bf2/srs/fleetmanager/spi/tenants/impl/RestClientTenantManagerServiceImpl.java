package org.bf2.srs.fleetmanager.spi.tenants.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import io.apicurio.multitenant.api.datamodel.NewRegistryTenantRequest;
import io.apicurio.multitenant.api.datamodel.RegistryTenant;
import io.apicurio.multitenant.api.datamodel.ResourceType;
import io.apicurio.multitenant.api.datamodel.TenantResource;
import io.apicurio.multitenant.api.datamodel.TenantStatusValue;
import io.apicurio.multitenant.api.datamodel.UpdateRegistryTenantRequest;
import io.apicurio.multitenant.client.TenantManagerClient;
import io.apicurio.multitenant.client.TenantManagerClientImpl;
import io.apicurio.multitenant.client.exception.RegistryTenantNotFoundException;
import io.apicurio.multitenant.client.exception.TenantManagerClientException;
import io.apicurio.rest.client.JdkHttpClientProvider;
import io.apicurio.rest.client.auth.Auth;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.auth.exception.AuthErrorHandler;
import io.apicurio.rest.client.config.ApicurioClientConfig;
import io.apicurio.rest.client.request.Operation;
import io.apicurio.rest.client.request.Request;
import io.apicurio.rest.client.spi.ApicurioHttpClient;
import io.micrometer.core.annotation.Timed;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.common.metrics.Constants;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.FaultToleranceConstants;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.tenants.model.Tenant;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantStatus;
import org.bf2.srs.fleetmanager.spi.tenants.model.UpdateTenantRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_TENANT_ID;

@UnlessBuildProfile("test")
@ApplicationScoped
public class RestClientTenantManagerServiceImpl implements TenantManagerService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.server-url.configured")
    String tenantManagerAuthServerUrl;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.client-id")
    String tenantManagerAuthClientId;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.secret")
    String tenantManagerAuthSecret;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.enabled")
    boolean tenantManagerAuthEnabled;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.ssl.ca.path")
    Optional<String> tenantManagerCAFilePath;

    private Auth auth;
    private Map<String, Object> clientConfigs;

    @ConfigProperty(name = "srs-fleet-manager.identity.server.resolver.enabled")
    Boolean resolveIdentityServer;

    @ConfigProperty(name = "srs-fleet-manager.identity.server.resolver.request-base-path")
    String resolverRequestBasePath;

    @ConfigProperty(name = "srs-fleet-manager.identity.server.resolver.request-path")
    String resolverRequestPath;

    private ApicurioHttpClient resolverHttpClient;

    // TODO Data is never deleted! Prevent OOM error.
    private Map<String, TenantManagerClientImpl> pool = new ConcurrentHashMap<String, TenantManagerClientImpl>();

    @PostConstruct
    void init() {

        if (resolveIdentityServer) {
            resolverHttpClient = new JdkHttpClientProvider().create(resolverRequestBasePath, Collections.emptyMap(), null, new AuthErrorHandler());
            final SsoProviders ssoProviders = resolverHttpClient.sendRequest(getSSOProviders());
            if (!tenantManagerAuthServerUrl.equals(ssoProviders.getTokenUrl())) {
                this.tenantManagerAuthServerUrl = ssoProviders.getTokenUrl();
            }
        }

        if (tenantManagerAuthEnabled) {
            log.info("Using Apicurio Registry REST TenantManagerClient with authentication enabled.");
            ApicurioHttpClient httpClient = new JdkHttpClientProvider().create(tenantManagerAuthServerUrl, Collections.emptyMap(), null, new AuthErrorHandler());
            this.auth = new OidcAuth(httpClient, tenantManagerAuthClientId, tenantManagerAuthSecret);
        } else {
            log.info("Using Apicurio Registry REST TenantManagerClient.");
            this.auth = null;
        }
        this.clientConfigs = new HashMap<>();
        if (tenantManagerCAFilePath.isPresent() && !tenantManagerCAFilePath.get().isBlank()) {
            clientConfigs.put(ApicurioClientConfig.APICURIO_REQUEST_CA_BUNDLE_LOCATION, tenantManagerCAFilePath.get());
        }
    }

    private TenantManagerClient getClient(TenantManagerConfig tm) {

        if (resolveIdentityServer) {

            final SsoProviders ssoProviders = resolverHttpClient.sendRequest(getSSOProviders());

            if (!ssoProviders.getTokenUrl().equals(tenantManagerAuthServerUrl)) {
                ApicurioHttpClient httpClient = new JdkHttpClientProvider().create(tenantManagerAuthServerUrl, Collections.emptyMap(), null, new AuthErrorHandler());
                this.auth = new OidcAuth(httpClient, tenantManagerAuthClientId, tenantManagerAuthSecret);
            }
        }

        return pool.computeIfAbsent(tm.getTenantManagerUrl(), k -> {
            return new TenantManagerClientImpl(tm.getTenantManagerUrl(), clientConfigs, auth);
        });
    }

    private Tenant convert(RegistryTenant data) {
        return Tenant.builder()
                .id(data.getTenantId())
                .status(TenantStatus.fromValue(data.getStatus().value()))
                .resources(convertToTenantLimit(data.getResources()))
                .build();
    }

    private List<TenantLimit> convertToTenantLimit(List<TenantResource> data) {
        var res = new ArrayList<TenantLimit>();
        for (TenantResource d : data) {
            res.add(TenantLimit.builder().type(d.getType().value()).limit(d.getLimit()).build());
        }
        return res;
    }

    private List<TenantResource> convertToTenantResource(List<TenantLimit> data) {
        var res = new ArrayList<TenantResource>();
        for (TenantLimit d : data) {
            var tr = new TenantResource();
            tr.setType(ResourceType.fromValue(d.getType()));
            tr.setLimit(d.getLimit());
            res.add(tr);
        }
        return res;
    }

    private UpdateRegistryTenantRequest convert(UpdateTenantRequest req) {
        var res = new UpdateRegistryTenantRequest();
        res.setStatus(TenantStatusValue.fromValue(req.getStatus().value()));
        res.setResources(convertToTenantResource(req.getResources()));
        return res;
    }

    private Request<SsoProviders> getSSOProviders() {
        return new Request.RequestBuilder<SsoProviders>()
                .operation(Operation.GET)
                .path(resolverRequestPath)
                .responseType(new TypeReference<SsoProviders>() {
                })
                .build();
    }

    @Timed(value = Constants.TENANT_MANAGER_CREATE_TENANT_TIMER, description = Constants.TENANT_MANAGER_DESCRIPTION)
    @Audited
    // @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @Retry(retryOn = {TenantManagerServiceException.class}) // 3 retries, 200ms jitter
    @Override
    public Tenant createTenant(TenantManagerConfig tm, CreateTenantRequest tenantRequest) throws TenantManagerServiceException {
        try {
            var client = getClient(tm);

            NewRegistryTenantRequest req = new NewRegistryTenantRequest();
            req.setOrganizationId(tenantRequest.getOrganizationId());
            req.setTenantId(tenantRequest.getTenantId());
            req.setCreatedBy(tenantRequest.getCreatedBy());

            req.setResources(Optional.ofNullable(tenantRequest.getResources()).stream()
                    .flatMap(Collection::stream)
                    .map(r -> {
                        TenantResource tr = new TenantResource();
                        tr.setType(ResourceType.fromValue(r.getType()));
                        tr.setLimit(r.getLimit());
                        return tr;
                    })
                    .collect(Collectors.toList()));

            RegistryTenant tenant = client.createTenant(req);

            return convert(tenant);
        } catch (TenantManagerClientException ex) {
            throw ExceptionConvert.convert(ex);
        }
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @Retry(retryOn = {TenantManagerServiceException.class}) // 3 retries, 200ms jitter
    @Override
    public Optional<Tenant> getTenantById(TenantManagerConfig tm, String tenantId) throws TenantManagerServiceException {
        try {
            var client = getClient(tm);
            RegistryTenant tenant = client.getTenant(tenantId);
            return Optional.of(convert(tenant));
        } catch (RegistryTenantNotFoundException ex) {
            return Optional.empty();
        } catch (TenantManagerClientException ex) {
            throw ExceptionConvert.convert(ex);
        }
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @Retry(retryOn = {TenantManagerServiceException.class}) // 3 retries, 200ms jitter
    @SuppressWarnings("deprecation")
    @Override
    public List<Tenant> getAllTenants(TenantManagerConfig tm) throws TenantManagerServiceException {
        try {
            var client = getClient(tm);
            return client.listTenants().stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
        } catch (TenantManagerClientException ex) {
            throw ExceptionConvert.convert(ex);
        }
    }

    @Audited
    //@Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @Retry(retryOn = {TenantManagerServiceException.class}) // 3 retries, 200ms jitter
    @Override
    public void updateTenant(TenantManagerConfig tm, UpdateTenantRequest req) throws TenantNotFoundServiceException, TenantManagerServiceException {
        try {
            var client = getClient(tm);
            var internalReq = convert(req);
            client.updateTenant(req.getId(), internalReq);
        } catch (RegistryTenantNotFoundException ex) {
            throw ExceptionConvert.convert(ex);
        } catch (TenantManagerClientException ex) {
            throw ExceptionConvert.convert(ex);
        }
    }

    @Timed(value = Constants.TENANT_MANAGER_DELETE_TENANT_TIMER, description = Constants.TENANT_MANAGER_DESCRIPTION)
    @Audited(extractParameters = {"1", KEY_TENANT_ID})
    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @Retry(retryOn = {TenantManagerServiceException.class}) // 3 retries, 200ms jitter
    @Override
    public void deleteTenant(TenantManagerConfig tm, String tenantId) throws TenantNotFoundServiceException, TenantManagerServiceException {
        try {
            var client = getClient(tm);
            client.deleteTenant(tenantId);
        } catch (RegistryTenantNotFoundException ex) {
            throw ExceptionConvert.convert(ex);
        } catch (TenantManagerClientException ex) {
            throw ExceptionConvert.convert(ex);
        }
    }

    @Override
    public boolean pingTenantManager(TenantManagerConfig tm) {
        // TODO implement
        return true;
    }

    @Override
    public boolean pingTenant(TenantManagerConfig tm, String tenantId) {
        // TODO implement
        return true;
    }

    /**
     * @see org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService#validateConfig(java.util.List)
     */
    @Override
    public void validateConfig(List<TenantLimit> limits) {
        if (limits == null) {
            return;
        }
        for (var limit : limits) {
            //this will throw an exception if any limit type is not valid
            ResourceType.fromValue(limit.getType());
        }
    }

    private static class SsoProviders {

        @JsonProperty("base_url")
        private String baseUrl;
        @JsonProperty("token_url")
        private String tokenUrl;
        @JsonProperty("jwks")
        private String jwks;
        @JsonProperty("valid_issuer")
        private String validIssuer;

        public SsoProviders() {
        }

        public SsoProviders(String baseUrl, String tokenUrl, String jwks, String validIssuer) {
            this.baseUrl = baseUrl;
            this.tokenUrl = tokenUrl;
            this.jwks = jwks;
            this.validIssuer = validIssuer;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public void setTokenUrl(String tokenUrl) {
            this.tokenUrl = tokenUrl;
        }

        public void setJwks(String jwks) {
            this.jwks = jwks;
        }

        public void setValidIssuer(String validIssuer) {
            this.validIssuer = validIssuer;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getTokenUrl() {
            return tokenUrl;
        }

        public String getJwks() {
            return jwks;
        }

        public String getValidIssuer() {
            return validIssuer;
        }
    }
}
