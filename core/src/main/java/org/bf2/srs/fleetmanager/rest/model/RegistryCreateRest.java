
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

import static lombok.AccessLevel.PACKAGE;

/**
 * Information used to create a new Service Registry instance within a multi-tenant deployment.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 * @see RegistryRest
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryCreateRest {

    /**
     * User-defined Registry name. Does not have to be unique.
     * <p>
     * (Optional)
     */
    @JsonProperty("name")
    @JsonPropertyDescription("User-defined Registry name. Does not have to be unique.")
    // TODO - names should be validated by kubernetes standard lenght etc.
    private String name;
}
