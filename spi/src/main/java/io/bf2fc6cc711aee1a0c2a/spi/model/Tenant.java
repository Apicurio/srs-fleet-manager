package io.bf2fc6cc711aee1a0c2a.spi.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Tenant information received from the Tenant Manager.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Tenant {

    /**
     * Tenant ID, unique per Registry Deployment.
     */
    String id;

    /**
     * URL of the Registry API endpoint for the given Tenant.
     */
    String tenantApiUrl;

    String authServerUrl;

    String authClientId;
}
