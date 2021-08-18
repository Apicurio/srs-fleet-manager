package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Service Registry instance within a multi-tenant deployment.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RegistryListDto extends AbstractList<RegistryDto> {

    @Builder
    public RegistryListDto(@NotNull List<RegistryDto> items, @NotNull Integer page, Integer size, Long total) {
        super(Kind.REGISTRY_LIST, items, page, size, total);
    }
}
