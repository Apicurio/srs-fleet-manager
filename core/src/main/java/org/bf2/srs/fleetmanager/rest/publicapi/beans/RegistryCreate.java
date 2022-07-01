
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root type for RegistryCreate
 * <p>
 * Information used to create a new Service Registry instance in a multi-tenant deployment.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "description"
})
@Generated("jsonschema2pojo")
public class RegistryCreate {

    /**
     * User-defined Registry instance name. Required. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry instance name. Required. Does not have to be unique.")
    private String name;
    /**
     * User-provided description of the new Service Registry instance. Not required.
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("User-provided description of the new Service Registry instance. Not required.")
    private String description;

    /**
     * User-defined Registry instance name. Required. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * User-defined Registry instance name. Required. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * User-provided description of the new Service Registry instance. Not required.
     * 
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * User-provided description of the new Service Registry instance. Not required.
     * 
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

}
