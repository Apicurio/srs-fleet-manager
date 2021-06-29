
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Service Registry instance within a multi-tenant deployment.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Registry extends ObjectReference {

    /**
     * (Required)
     */
    @NotNull
    private RegistryStatusValue status;

    /**
     * (Required)
     */
    @NotEmpty
    private String registryUrl;

    /**
     * User-defined Registry name. Does not have to be unique.
     * <p>
     * (Optional)
     */
    private String name;

    /**
     * Identifier of a multi-tenant deployment, where this Service Registry instance resides.
     * <p>
     * (Optional)
     */
    private Long registryDeploymentId;

    /**
     * Registry instance owner
     * <p>
     * (Optional)
     */
    private String owner;

    @Builder
    public Registry(@NotNull String id, @NotNull String kind, String href, @NotNull RegistryStatusValue status, @NotEmpty String registryUrl, String name, Long registryDeploymentId, @NotNull String owner) {
        super(id, kind, href);
        this.status = status;
        this.registryUrl = registryUrl;
        this.name = name;
        this.registryDeploymentId = registryDeploymentId;
        this.owner = owner;
    }
}
