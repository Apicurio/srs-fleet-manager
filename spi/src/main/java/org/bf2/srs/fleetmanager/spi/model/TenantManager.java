package org.bf2.srs.fleetmanager.spi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PACKAGE;

/**
 * Represents information about given Tenant Manager, one for every Registry Deployment.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TenantManager {

    String tenantManagerUrl;

    String registryDeploymentUrl;
}
