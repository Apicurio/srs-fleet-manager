
package org.bf2.srs.fleetmanager.rest.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "firstExecuteAt",
        "priority",
        "intervalSec"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TaskSchedule {

    /**
     * ISO 8601 UTC timestamp.
     * <p>
     * (Required)
     */
    @JsonProperty("firstExecuteAt")
    @JsonPropertyDescription("ISO 8601 UTC timestamp.")
    @NotEmpty
    private String firstExecuteAt;

    /**
     * Higher number means higher priority. Default priority is 5.
     * <p>
     * (Optional)
     */
    @JsonProperty("priority")
    @JsonPropertyDescription("Higher number means higher priority. Default priority is 5.")
    private Integer priority;

    /**
     * (Optional)
     */
    @JsonProperty("intervalSec")
    @JsonPropertyDescription("")
    private Long intervalSec;
}
