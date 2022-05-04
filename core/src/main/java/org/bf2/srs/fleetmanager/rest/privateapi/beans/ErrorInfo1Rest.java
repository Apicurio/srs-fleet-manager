
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Type for ErrorInfo
 * <p>
 * Details about a specific error returned by the server.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "error_code",
    "message"
})
@Generated("jsonschema2pojo")
public class ErrorInfo1Rest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("message")
    private String message;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("error_code")
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("error_code")
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

}
