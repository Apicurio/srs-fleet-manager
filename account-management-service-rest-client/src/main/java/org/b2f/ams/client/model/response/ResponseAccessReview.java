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
import org.b2f.ams.client.model.Action;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "account_id",
        "action",
        "allowed",
        "cluster_id",
        "cluster_uuid",
        "organization_id",
        "resource_type",
        "subscription_id",
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ResponseAccessReview {

    /**
     * (Optional)
     */
    @JsonProperty("account_id")
    @JsonPropertyDescription("")
    String accountId;

    /**
     * (Optional)
     */
    @JsonProperty("account_username")
    @JsonPropertyDescription("")
    Action accountUsername;

    /**
     * (Required)
     */
    @JsonProperty("allowed")
    @JsonPropertyDescription("")
    @NotNull
    Boolean allowed;

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
    @JsonProperty("resource_type")
    @JsonPropertyDescription("")
    String resourceType;

    /**
     * (Optional)
     */
    @JsonProperty("subscription_id")
    @JsonPropertyDescription("")
    String subscriptionId;
}
