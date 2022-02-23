package org.bf2.srs.fleetmanager.spi.ams.impl.model.request;

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
        "account_username",
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
public class TermsReview {

    /**
     * (Required)
     */
    @JsonProperty("account_username")
    @JsonPropertyDescription("")
    @NotNull
    String accountUsername;

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
