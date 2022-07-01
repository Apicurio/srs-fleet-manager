
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "kind",
    "href"
})
@Generated("jsonschema2pojo")
public class ObjectReference {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    private String id;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("kind")
    private String kind;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("href")
    private String href;

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
    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

}
