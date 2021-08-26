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
public class ErrorListDto extends AbstractList<ErrorDto> {

    @Builder
    public ErrorListDto(@NotNull List<ErrorDto> items, @NotNull Integer page, Integer size, Long total) {
        super(Kind.ERROR_LIST, items, page, size, total);
    }
}
