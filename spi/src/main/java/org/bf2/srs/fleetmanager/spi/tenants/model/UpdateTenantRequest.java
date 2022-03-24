package org.bf2.srs.fleetmanager.spi.tenants.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static lombok.AccessLevel.PACKAGE;

@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class UpdateTenantRequest {

    String id;

    TenantStatus status;

    /**
     * List of resource limits to apply to this tenant
     */
    List<TenantLimit> resources;
}
