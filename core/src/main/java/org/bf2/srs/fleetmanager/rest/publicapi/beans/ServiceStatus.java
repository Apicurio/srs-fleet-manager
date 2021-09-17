
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Type for ServiceStatus
 * <p>
 * Schema for the service status response body
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "max_instances_reached"
})
@Generated("jsonschema2pojo")
public class ServiceStatus {

    /**
     * Boolean property indicating if the maximum number of total instances have been reached, therefore creation of more instances should not be allowed.
     * 
     */
    @JsonProperty("max_instances_reached")
    @JsonPropertyDescription("Boolean property indicating if the maximum number of total instances have been reached, therefore creation of more instances should not be allowed.")
    private Boolean maxInstancesReached;

    /**
     * Boolean property indicating if the maximum number of total instances have been reached, therefore creation of more instances should not be allowed.
     * 
     */
    @JsonProperty("max_instances_reached")
    public Boolean getMaxInstancesReached() {
        return maxInstancesReached;
    }

    /**
     * Boolean property indicating if the maximum number of total instances have been reached, therefore creation of more instances should not be allowed.
     * 
     */
    @JsonProperty("max_instances_reached")
    public void setMaxInstancesReached(Boolean maxInstancesReached) {
        this.maxInstancesReached = maxInstancesReached;
    }

}
