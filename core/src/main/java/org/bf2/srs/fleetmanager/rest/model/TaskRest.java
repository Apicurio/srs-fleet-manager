
package org.bf2.srs.fleetmanager.rest.model;

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
        "id",
        "type",
        "data",
        "schedule"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TaskRest {

    /**
     * (Required)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    @NotEmpty
    private String id;

    /**
     * (Required)
     */
    @JsonProperty("type")
    @JsonPropertyDescription("")
    @NotEmpty
    private String type;

    /**
     * (Required)
     */
    @JsonProperty("data")
    @JsonPropertyDescription("")
    @NotEmpty
    private String data;

    /**
     * (Required)
     */
    @JsonProperty("schedule")
    @JsonPropertyDescription("")
    @NotEmpty
    private TaskScheduleRest schedule;
}
