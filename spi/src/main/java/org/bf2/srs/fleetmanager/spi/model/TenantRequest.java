package org.bf2.srs.fleetmanager.spi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PACKAGE;

@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
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
