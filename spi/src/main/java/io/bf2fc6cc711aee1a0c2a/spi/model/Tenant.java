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
     * Auth server url (including realm), required by apicurio-registry for the authentication
     */
    String authServerUrl;

    /**
     * ClientId in the tenant's realm, used by apicurio-registry to validate incoming tokens
     */
    String authClientId;
}
