
package io.bf2fc6cc711aee1a0c2a.rest.model;

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
    private Long id;

    /**
     * (Required)
     */
    @JsonProperty("status")
    @JsonPropertyDescription("")
    @NotNull
    private RegistryStatusRest status;

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
     * (Optional)
     */
    @JsonProperty("registryDeploymentId")
    @JsonPropertyDescription("")
    private Long registryDeploymentId;
}
