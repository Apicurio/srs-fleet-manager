package org.b2f.ams.client.model.response;

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
        "account_id",
        "organization_id",
        "redirect_url",
        "terms_available",
        "terms_required",
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ResponseTermsReview {

    /**
     * (Required)
     */
    @JsonProperty("account_id")
    @JsonPropertyDescription("")
    @NotNull
    String accountId;
    /**
     * (Required)
     */
    @JsonProperty("organization_id")
    @JsonPropertyDescription("")
    @NotNull
    String organizationId;
    /**
     * (Optional)
     */
    @JsonProperty("redirect_url")
    @JsonPropertyDescription("")
    String redirectUrl;
    /**
     * (Required)
     */
    @JsonProperty("terms_available")
    @JsonPropertyDescription("")
    @NotNull
    Boolean termsAvailable;
    /**
     * (Required)
     */
    @JsonProperty("terms_required")
    @JsonPropertyDescription("")
    @NotNull
    Boolean termsRequired;
}
