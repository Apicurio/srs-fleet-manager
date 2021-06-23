
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ObjectReference {

    @NotNull
    private String id;

    @NotNull
    private String kind;

    private String href;
}
