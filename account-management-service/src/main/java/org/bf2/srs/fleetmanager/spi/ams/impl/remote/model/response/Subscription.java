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
        "id",
        "kind",
        "href"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Subscription {

    /**
     * (Required)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("")
    @NotNull
    String id;

    /**
     * (Optional)
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("")
    String kind;

    /**
     * (Optional)
     */
    @JsonProperty("href")
    @JsonPropertyDescription("")
    String href;
}
