package org.bf2.srs.fleetmanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

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
    private List<RegistryRest> items;

    /**
     * (Required)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("Kind of the service")
    @NotNull
    private String kind = "ServiceRegistryList";

    /**
     * (Optional)
     */
    @JsonProperty("page")
    @JsonPropertyDescription("")
    @NotEmpty
    private String page;

    /**
     * (Optional)
     */
    @JsonProperty("size")
    @JsonPropertyDescription("Size of the current view of items")
    private String size;

    /**
     * (Optional)
     */
    @JsonProperty("total")
    @JsonPropertyDescription("Total number of items in list")
    private String total;

}
