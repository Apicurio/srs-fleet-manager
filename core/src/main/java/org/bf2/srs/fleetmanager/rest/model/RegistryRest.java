
package org.bf2.srs.fleetmanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
 * Service Registry instance within a multi-tenant deployment.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "registryUrl",
        "status",
        "registryDeploymentId"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryRest {

    /**
     * (Required)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    @NotNull
    private String id;

    /**
     * (Required)
     */
    @JsonProperty("status")
    @JsonPropertyDescription("")
    @NotNull
    private String status;

    /**
     * (Required)
     */
    @JsonProperty("registryUrl")
    @JsonPropertyDescription("")
    @NotEmpty
    private String registryUrl;

    /**
     * User-defined Registry name. Does not have to be unique.
     * <p>
     * (Optional)
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry name. Does not have to be unique.")
    private String name;

    /**
     * Identifier of a multi-tenant deployment, where this Service Registry instance resides.
     * <p>
     * (Optional)
     */
    @JsonProperty("registryDeploymentId")
    @JsonPropertyDescription("Identifier of a multi-tenant deployment, where this Service Registry instance resides.")
    private Long registryDeploymentId;
}
