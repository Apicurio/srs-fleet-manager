package org.bf2.srs.fleetmanager.spi.ams.impl.remote.model;

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
        "kind"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ObjectReference {

    /**
     * (Optional)
     */
    @JsonProperty("href")
    @JsonPropertyDescription("")
    @NotNull
    private String href;
    /**
     * (Required)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    @NotNull
    private String id;
    /**
     * (Required)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("")
    @NotNull
    private String kind;
}
