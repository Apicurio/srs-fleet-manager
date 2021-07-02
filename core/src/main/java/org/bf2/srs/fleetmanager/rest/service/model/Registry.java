
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
    @Pattern(regexp = "accepted|provisioning|ready|failed|deprovision|deleting")
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

    /**
     * This value is set by the storage layer.
     */
    private Instant createdAt;

    /**
     * This value is updated by the storage layer.
     */
    private Instant updatedAt;

    private String description;

    /**
     * Registry instance org id
     * <p>
     * (Optional)
     */
    private String orgId;

    @Builder
    public Registry(@NotNull String id, @NotNull String kind, String href,
                    @NotNull RegistryStatusValue status, @NotEmpty String registryUrl, String name, @NotNull String owner,
                    Long registryDeploymentId, Instant createdAt, Instant updatedAt, String description, @NotNull String orgId) {
        super(id, kind, href);
        this.status = status;
        this.registryUrl = registryUrl;
        this.name = name;
        this.registryDeploymentId = registryDeploymentId;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.orgId = orgId;
    }
}
