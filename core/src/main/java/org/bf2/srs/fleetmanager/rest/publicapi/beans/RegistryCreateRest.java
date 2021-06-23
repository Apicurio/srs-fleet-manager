
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Type for RegistryCreate
 * <p>
 * Information used to create a new Service Registry instance within a multi-tenant deployment.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name"
})
@Generated("jsonschema2pojo")
public class RegistryCreateRest {

    /**
     * User-defined Registry name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry name. Does not have to be unique.")
    private String name;

    /**
     * User-defined Registry name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * User-defined Registry name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

}
