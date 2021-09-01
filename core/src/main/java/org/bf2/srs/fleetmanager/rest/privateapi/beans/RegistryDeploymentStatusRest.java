
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import java.util.Date;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "lastUpdated",
    "value"
})
@Generated("jsonschema2pojo")
public class RegistryDeploymentStatusRest {

    /**
     * ISO 8601 UTC timestamp.
     * (Required)
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("lastUpdated")
    @JsonPropertyDescription("ISO 8601 UTC timestamp.")
    private Date lastUpdated;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("value")
    @JsonPropertyDescription("")
    private RegistryDeploymentStatusValueRest value;

    /**
     * ISO 8601 UTC timestamp.
     * (Required)
     * 
     */
    @JsonProperty("lastUpdated")
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * ISO 8601 UTC timestamp.
     * (Required)
     * 
     */
    @JsonProperty("lastUpdated")
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("value")
    public RegistryDeploymentStatusValueRest getValue() {
        return value;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("value")
    public void setValue(RegistryDeploymentStatusValueRest value) {
        this.value = value;
    }

}
