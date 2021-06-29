
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
     * <p>
     * (Optional)
     */
    // TODO Make required?
    // TODO https://kubernetes.io/docs/concepts/overview/working-with-objects/names/
    @Size(max = 253, message = "Registry instance name must not be longer than 253 characters.")
    private String name;

    /**
     * Registry owner name.
     * <p>
     */
    private String owner;

    /**
     * Optional
     * Non-unique
     */
    @Size(max = 255, message = "Registry instance description must not be longer than 255 characters.")
    private String description;
}
