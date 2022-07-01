
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reason",
    "operation_id",
    "id",
    "kind",
    "href",
    "code"
})
@Generated("jsonschema2pojo")
public class Error {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("operation_id")
    private String operationId;
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
    @JsonProperty("code")
    private String code;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("operation_id")
    public String getOperationId() {
        return operationId;
    }

    @JsonProperty("operation_id")
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

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

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

}
