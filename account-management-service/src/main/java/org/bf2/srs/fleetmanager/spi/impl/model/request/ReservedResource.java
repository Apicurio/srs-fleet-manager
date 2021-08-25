package org.bf2.srs.fleetmanager.spi.impl.model.request;

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
import org.bf2.srs.fleetmanager.spi.impl.model.ObjectReference;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "href",
        "id",
        "kind",
        "availability_zone_type",
        "billing_model",
        "byoc",
        "cluster",
        "count",
        "created_at",
        "resource_name",
        "resource_type",
        "subscription",
        "updated_at"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ReservedResource {

    /**
     * (Optional)
     */
    @JsonProperty("href")
    @JsonPropertyDescription("")
    private String href;
    /**
     * (Optional)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    private String id;
    /**
     * (Optional)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("")
    private String kind;
    /**
     * (Optional)
     */
    @JsonProperty("availability_zone")
    @JsonPropertyDescription("")
    private String availabilityZone;
    /**
     * (Optional)
     */
    @JsonProperty("billing_model")
    @JsonPropertyDescription("")
    private String billingModel;
    /**
     * (Required)
     */
    @JsonProperty("byoc")
    @JsonPropertyDescription("")
    @NotNull
    private Boolean byoc;
    /**
     * (Optional)
     */
    @JsonProperty("cluster")
    @JsonPropertyDescription("")
    private Boolean cluster;
    /**
     * (Optional)
     */
    @JsonProperty("count")
    @JsonPropertyDescription("")
    private Integer count;
    /**
     * (Optional)
     */
    @JsonProperty("create_at")
    @JsonPropertyDescription("")
    private String createdAt;
    /**
     * (Optional)
     */
    @JsonProperty("resource_name")
    @JsonPropertyDescription("")
    private String resourceName;
    /**
     * (Optional)
     */
    @JsonProperty("resource_type")
    @JsonPropertyDescription("")
    private String resourceType;
    /**
     * (Optional)
     */
    @JsonProperty("subscription")
    @JsonPropertyDescription("")
    private ObjectReference subscription;
    /**
     * (Optional)
     */
    @JsonProperty("updated_at")
    @JsonPropertyDescription("")
    private String updatedAt;
}
