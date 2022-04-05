package org.bf2.srs.fleetmanager.spi.tenants.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static lombok.AccessLevel.PACKAGE;

/**
 * Tenant information received from the Tenant Manager.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Tenant {

    /**
     * Tenant ID, unique per Registry Deployment.
     */
    String id;

    @Setter
    TenantStatus status;

    List<TenantLimit> resources;
}
