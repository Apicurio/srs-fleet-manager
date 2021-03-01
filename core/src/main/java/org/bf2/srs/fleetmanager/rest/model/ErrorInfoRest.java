
package org.bf2.srs.fleetmanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;

import static lombok.AccessLevel.PACKAGE;

/**
 * Entity for general error result.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "error_code",
        "message"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ErrorInfoRest {

    /**
     * (Required)
     */
    @JsonProperty("error_code")
    @Min(100)
    private Integer errorCode;

    /**
     * (Optional)
     */
    @JsonProperty("message")
    private String message;
}
