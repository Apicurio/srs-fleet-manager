package org.b2f.ams.client.model.request;


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
        "event_code",
        "site_code"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SelfTermsReview {

    /**
     * (Required)
     */
    @JsonProperty("event_code")
    @JsonPropertyDescription("")
    @NotNull
    String eventCode;

    /**
     * (Required)
     */
    @JsonProperty("site_code")
    @JsonPropertyDescription("")
    @NotNull
    String siteCode;
}
