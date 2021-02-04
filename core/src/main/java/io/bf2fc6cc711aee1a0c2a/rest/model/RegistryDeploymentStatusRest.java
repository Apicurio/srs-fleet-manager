
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
        "status",
        "lastUpdated"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryDeploymentStatusRest {

    /**
     * (Required)
     */
    @JsonProperty("value")
    @JsonPropertyDescription("")
    @NotNull
    private RegistryDeploymentStatusValueRest value;

    /**
     * ISO 8601 UTC timestamp.
     * <p>
     * (Required)
     */
    @JsonProperty("lastUpdated")
    @JsonPropertyDescription("ISO 8601 UTC timestamp.")
    @NotEmpty
    private String lastUpdated;
}
