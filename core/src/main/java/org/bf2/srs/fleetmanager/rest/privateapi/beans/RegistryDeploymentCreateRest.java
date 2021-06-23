
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Information used to create (register) a new multi-tenant Service Registry deployment, that can host Service Registry instances.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "registryDeploymentUrl",
    "tenantManagerUrl",
    "name"
})
@Generated("jsonschema2pojo")
public class RegistryDeploymentCreateRest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("registryDeploymentUrl")
    @JsonPropertyDescription("")
    private String registryDeploymentUrl;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tenantManagerUrl")
    @JsonPropertyDescription("")
    private String tenantManagerUrl;
    /**
     * User-defined Registry Deployment name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry Deployment name. Does not have to be unique.")
    private String name;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("registryDeploymentUrl")
    public String getRegistryDeploymentUrl() {
        return registryDeploymentUrl;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("registryDeploymentUrl")
    public void setRegistryDeploymentUrl(String registryDeploymentUrl) {
        this.registryDeploymentUrl = registryDeploymentUrl;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tenantManagerUrl")
    public String getTenantManagerUrl() {
        return tenantManagerUrl;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tenantManagerUrl")
    public void setTenantManagerUrl(String tenantManagerUrl) {
        this.tenantManagerUrl = tenantManagerUrl;
    }

    /**
     * User-defined Registry Deployment name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * User-defined Registry Deployment name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

}
