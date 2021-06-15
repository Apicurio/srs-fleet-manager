package org.bf2.srs.fleetmanager.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public abstract class AbstractList {

    /**
     * (Required)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("Kind of the service")
    @NotNull
    protected String kind;

     /**
     * (Optional)
     */
    @JsonProperty("page")
    @JsonPropertyDescription("")
    @NotEmpty
    protected String page;

    /**
     * (Optional)
     */
    @JsonProperty("size")
    @JsonPropertyDescription("Size of the current view of items")
    protected String size;

    /**
     * (Optional)
     */
    @JsonProperty("total")
    @JsonPropertyDescription("Total number of items in list")
    protected Long total;

}