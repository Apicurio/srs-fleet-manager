
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    // TODO - names should be validated by kubernetes standard length etc.
    private String name;
}
