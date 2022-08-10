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

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "account_username",
        "action",
        "resource_type",
        "cluster_id",
        "cluster_uuid",
        "organization_id",
        "subscription_id"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AccessReview {

    /**
     * (Required)
     */
    @JsonProperty("account_username")
    @JsonPropertyDescription("")
    @NotNull
    String accountUsername;

    /**
     * (Required)
     */
    @JsonProperty("action")
    @JsonPropertyDescription("")
    @NotNull
    String action;

    /**
     * (Required)
     */
    @JsonProperty("resource_type")
    @JsonPropertyDescription("")
    @NotNull
    String resourceType;

    /**
     * (Optional)
     */
    @JsonProperty("cluster_id")
    @JsonPropertyDescription("")
    String clusterId;

    /**
     * (Optional)
     */
    @JsonProperty("cluster_uuid")
    @JsonPropertyDescription("")
    String clusterUuid;

    /**
     * (Optional)
     */
    @JsonProperty("organization_id")
    @JsonPropertyDescription("")
    String organizationId;


    /**
     * (Optional)
     */
    @JsonProperty("subscription_id")
    @JsonPropertyDescription("")
    String subscriptionId;
}