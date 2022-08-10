package org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response;

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

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "href",
        "id",
        "kind",
        "code",
        "operationId",
        "reason"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Error {

    /**
     * (Optional)
     */
    @JsonProperty("href")
    @JsonPropertyDescription("")
    private String href;
    /**
     * (Optional)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    private String id;
    /**
     * (Optional)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("")
    @NotNull
    private String kind;
    /**
     * (Optional)
     */
    @JsonProperty("code")
    @JsonPropertyDescription("")
    private String code;
    /**
     * (Optional)
     */
    @JsonProperty("operation_id")
    @JsonPropertyDescription("")
    private String operationId;
    /**
     * (Optional)
     */
    @JsonProperty("reason")
    @JsonPropertyDescription("")
    private String reason;
}
