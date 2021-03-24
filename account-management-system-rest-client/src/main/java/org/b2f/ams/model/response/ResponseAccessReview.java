package org.b2f.ams.model.response;

import org.b2f.ams.model.request.AccessReview;

public class ResponseAccessReview {

    String accountUsername;
    AccessReview.Action action;
    String clusterId;
    String clusterUuid;
    String organizationId;
    String resourceType;
    String subscriptionId;
}
