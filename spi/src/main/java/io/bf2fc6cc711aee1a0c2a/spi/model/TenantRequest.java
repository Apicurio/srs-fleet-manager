package io.bf2fc6cc711aee1a0c2a.spi.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TenantRequest {

    /**
     * TenantId is specified by the control-plane
     */
    String tenantId;

    /**
     * Auth server url (including realm), required by apicurio-registry for the authentication
     */
    String authServerUrl;

    /**
     * ClientId in the tenant's realm, used by apicurio-registry to validate incoming tokens
     */
    String authClientId;

}
