package org.bf2.srs.fleetmanager.spi.ams.impl.model.response;

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
import java.util.List;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "allowed",
        "organization_id",
        "subscription",
        "excess_resources"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClusterAuthorizationResponse {

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
    @JsonProperty("organization_id")
    @JsonPropertyDescription("")
    String organizationId;

    /**
     * (Optional)
     */
    @JsonProperty("subscription")
    @JsonPropertyDescription("")
    Subscription subscription;

    /**
     * (Optional)
     */
    @JsonProperty("excess_resources")
    @JsonPropertyDescription("")
    List<ExcessResource> excessResources;
}
