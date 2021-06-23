
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Type for TaskSchedule
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "firstExecuteAt",
    "priority",
    "intervalSec"
})
@Generated("jsonschema2pojo")
public class TaskScheduleRest {

    /**
     * ISO 8601 UTC timestamp.
     * (Required)
     * 
     */
    @JsonProperty("firstExecuteAt")
    @JsonPropertyDescription("ISO 8601 UTC timestamp.")
    private String firstExecuteAt;
    /**
     * Higher number means higher priority. Default priority is 5.
     * 
     */
    @JsonProperty("priority")
    @JsonPropertyDescription("Higher number means higher priority. Default priority is 5.")
    private Integer priority;
    @JsonProperty("intervalSec")
    private Integer intervalSec;

    /**
     * ISO 8601 UTC timestamp.
     * (Required)
     * 
     */
    @JsonProperty("firstExecuteAt")
    public String getFirstExecuteAt() {
        return firstExecuteAt;
    }

    /**
     * ISO 8601 UTC timestamp.
     * (Required)
     * 
     */
    @JsonProperty("firstExecuteAt")
    public void setFirstExecuteAt(String firstExecuteAt) {
        this.firstExecuteAt = firstExecuteAt;
    }

    /**
     * Higher number means higher priority. Default priority is 5.
     * 
     */
    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    /**
     * Higher number means higher priority. Default priority is 5.
     * 
     */
    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("intervalSec")
    public Integer getIntervalSec() {
        return intervalSec;
    }

    @JsonProperty("intervalSec")
    public void setIntervalSec(Integer intervalSec) {
        this.intervalSec = intervalSec;
    }

}
