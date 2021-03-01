
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

import static lombok.AccessLevel.PACKAGE;

/**
 * Registry Deployment entity for CREATE operation.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 * @see org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeployment
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "registryDeploymentUrl",
        "tenantManagerUrl",
        "name"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryDeploymentCreateRest {

    /**
     * (Required)
     */
    @JsonProperty("registryDeploymentUrl")
    @JsonPropertyDescription("")
    @NotEmpty
    private String registryDeploymentUrl;

    /**
     * (Required)
     */
    @JsonProperty("tenantManagerUrl")
    @JsonPropertyDescription("")
    @NotEmpty
    private String tenantManagerUrl;

    /**
     * User-defined Registry Deployment name. Does not have to be unique.
     * <p>
     * (Optional)
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry Deployment name. Does not have to be unique.")
    private String name;
}
