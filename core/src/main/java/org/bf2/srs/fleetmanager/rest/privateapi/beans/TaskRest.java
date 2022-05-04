
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "type",
    "data",
    "schedule"
})
@Generated("jsonschema2pojo")
public class TaskRest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    private String id;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("")
    private String type;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data")
    @JsonPropertyDescription("")
    private String data;
    /**
     * Root Type for TaskSchedule
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("schedule")
    @JsonPropertyDescription("")
    private TaskScheduleRest schedule;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data")
    public String getData() {
        return data;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("data")
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Root Type for TaskSchedule
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("schedule")
    public TaskScheduleRest getSchedule() {
        return schedule;
    }

    /**
     * Root Type for TaskSchedule
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("schedule")
    public void setSchedule(TaskScheduleRest schedule) {
        this.schedule = schedule;
    }

}
