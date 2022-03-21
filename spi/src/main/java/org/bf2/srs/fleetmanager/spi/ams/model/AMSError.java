package org.bf2.srs.fleetmanager.spi.ams.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AMSError {

    private String code;

    private String reason;
}
