package io.bf2fc6cc711aee1a0c2a.spi.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents information about given Tenant Manager, one for every Registry Deployment.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TenantManager {

    String tenantManagerUrl;

    String registryDeploymentUrl;
}
