package org.b2f.ams.client.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.b2f.ams.client.model.Action;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "account_username",
        "action",
        "clusterId",
        "status",
        "registry_deployment_id"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ResponseAccessReview {

    String accountUsername;
    Action action;
    String clusterId;
    String clusterUuid;
    String organizationId;
    String resourceType;
    String subscriptionId;
}
