package org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request;

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

import java.util.List;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "account_username",
        "availability_zone",
        "byoc",
        "cloud_account_id",
        "cloud_provider_id",
        "cluster_id",
        "disconnected",
        "display_name",
        "external_cluster_id",
        "managed",
        "product_category",
        "product_id",
        "reserve",
        "resources"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClusterAuthorization {

    /**
     * (Required)
     */
    @JsonProperty("account_username")
    @JsonPropertyDescription("")
    @NotNull
    private String accountUsername;
    /**
     * (Optional)
     */
    @JsonProperty("availability_zone")
    @JsonPropertyDescription("")
    private String availabilityZone;
    /**
     * (Required)
     */
    @JsonProperty("byoc")
    @JsonPropertyDescription("")
    private Boolean byoc;
    /**
     * (Optional)
     */
    @JsonProperty("cloud_account_id")
    @JsonPropertyDescription("")
    private String cloudAccountId;
    /**
     * (Optional)
     */
    @JsonProperty("cloud_provider_id")
    @JsonPropertyDescription("")
    private String cloudProviderId;
    /**
     * (Required)
     */
    @JsonProperty("cluster_id")
    @JsonPropertyDescription("")
    @NotNull
    private String clusterId;
    /**
     * (Optional)
     */
    @JsonProperty("disconnected")
    @JsonPropertyDescription("")
    private Boolean disconnected;
    /**
     * (Optional)
     */
    @JsonProperty("display_name")
    @JsonPropertyDescription("")
    private String displayName;
    /**
     * (Optional)
     */
    @JsonProperty("external_cluster_id")
    @JsonPropertyDescription("")
    private String externalClusterId;
    /**
     * (Optional)
     */
    @JsonProperty("managed")
    @JsonPropertyDescription("")
    private Boolean managed;
    /**
     * (Optional)
     */
    @JsonProperty("product_category")
    @JsonPropertyDescription("")
    private String productCategory;
    /**
     * (Optional)
     */
    @JsonProperty("product_id")
    @JsonPropertyDescription("")
    private String productId;
    /**
     * (Optional)
     */
    @JsonProperty("reserve")
    @JsonPropertyDescription("")
    private Boolean reserve;
    /**
     * (Optional)
     */
    @JsonProperty("resources")
    @JsonPropertyDescription("")
    private List<ReservedResource> resources;
}
