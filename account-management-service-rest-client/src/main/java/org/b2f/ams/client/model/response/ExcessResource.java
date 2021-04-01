package org.b2f.ams.client.model.response;

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

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "href",
        "id",
        "kind",
        "availability_zone_type",
        "billing_model",
        "byoc",
        "count",
        "resource_name",
        "resource_type"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ExcessResource {

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
    @JsonProperty("availability_zone_type")
    @JsonPropertyDescription("")
    private String availabilityZoneType;
    /**
     * (Optional)
     */
    @JsonProperty("billing_model")
    @JsonPropertyDescription("")
    private String billingModel;
    /**
     * (Optional)
     */
    @JsonProperty("byoc")
    @JsonPropertyDescription("")
    private Boolean byoc;
    /**
     * (Optional)
     */
    @JsonProperty("count")
    @JsonPropertyDescription("")
    private Integer count;
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
}
