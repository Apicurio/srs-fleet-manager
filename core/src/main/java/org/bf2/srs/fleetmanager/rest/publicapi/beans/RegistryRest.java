
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Type for Registry
 * <p>
 * Service Registry instance within a multi-tenant deployment.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "kind",
    "href",
    "status",
    "registryUrl",
    "name",
    "registryDeploymentId"
})
@Generated("jsonschema2pojo")
public class RegistryRest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("href")
    private String href;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    @JsonPropertyDescription("")
    private RegistryStatusValueRest status;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("registryUrl")
    private String registryUrl;
    /**
     * User-defined Registry name. Does not have to be unique.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry name. Does not have to be unique.")
    private String name;
    /**
     * Identifier of a multi-tenant deployment, where this Service Registry instance resides.
     * 
     */
    @JsonProperty("registryDeploymentId")
    @JsonPropertyDescription("Identifier of a multi-tenant deployment, where this Service Registry instance resides.")
    private Integer registryDeploymentId;

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

    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public RegistryStatusValueRest getStatus() {
        return status;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(RegistryStatusValueRest status) {
        this.status = status;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("registryUrl")
    public String getRegistryUrl() {
        return registryUrl;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("registryUrl")
    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

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

    /**
     * Identifier of a multi-tenant deployment, where this Service Registry instance resides.
     * 
     */
    @JsonProperty("registryDeploymentId")
    public Integer getRegistryDeploymentId() {
        return registryDeploymentId;
    }

    /**
     * Identifier of a multi-tenant deployment, where this Service Registry instance resides.
     * 
     */
    @JsonProperty("registryDeploymentId")
    public void setRegistryDeploymentId(Integer registryDeploymentId) {
        this.registryDeploymentId = registryDeploymentId;
    }

}
