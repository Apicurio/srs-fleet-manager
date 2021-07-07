
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryCreate {

    /**
     * User-defined Registry name. Does not have to be unique.
     * TODO Make unique within an organization.
     */
    @Size(min = 1, max = 32, message = "Registry instance name length must be between 1 and 32 characters (inclusive).")
    @Pattern(regexp = "[a-z]([a-z0-9\\-]*[a-z0-9])?", message = "Registry instance must only consist of lower case, alphanumeric characters and '-'. " +
            "Must start with an alphabetic character. Must end with an alphanumeric character.")
    private String name;

    /**
     * Registry owner name.
     * <p>
     */
    private String owner;

    /**
     * Registry owner id.
     * <p>
     */
    private Long ownerId;

    /**
     * Registry org id.
     * <p>
     *
     */
    private String orgId;

    /**
     * Optional
     * Non-unique
     */
    @Size(max = 255, message = "Registry instance description must not be longer than 255 characters.")
    private String description;
}
