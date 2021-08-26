
package org.bf2.srs.fleetmanager.rest.service.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErrorDto extends ObjectReferenceDto {

    private String code;

    private String operationId;

    private String reason;

    @Builder
    public ErrorDto(@NotNull String id, @NotNull String code, String operationId, @NotNull String reason) {
        super(id, Kind.ERROR);
        this.code = code;
        this.operationId = operationId;
        this.reason = reason;
    }

    @Override
    public String getHref() {
        return "/api/connector_mgmt/v1/errors/" + getId();
    }
}
