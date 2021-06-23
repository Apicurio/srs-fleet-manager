
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Multi-tenant Service Registry deployment, that can host Service Registry instances.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "tenantManagerUrl",
    "registryDeploymentUrl",
    "status",
    "name"
})
@Generated("jsonschema2pojo")
public class RegistryDeploymentRest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    private Integer id;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("tenantManagerUrl")
    @JsonPropertyDescription("")
    private String tenantManagerUrl;
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
    @JsonProperty("status")
    @JsonPropertyDescription("")
    private RegistryDeploymentStatusRest status;
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
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
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
    @JsonProperty("status")
    public RegistryDeploymentStatusRest getStatus() {
        return status;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(RegistryDeploymentStatusRest status) {
        this.status = status;
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
