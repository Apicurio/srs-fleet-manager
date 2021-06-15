package org.bf2.srs.fleetmanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
 * Service Registry instance within a multi-tenant deployment.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kind",
        "items",
        "page",
        "size",
        "total"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryRestList extends AbstractList {

    /**
     * (Required)
     */
    @JsonProperty("items")
    @JsonPropertyDescription("")
    @NotNull
    private RegistryRest items;

}
