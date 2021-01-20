package io.bf2fc6cc711aee1a0c2a.rest.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class StatusRest {

    /**
     * ISO 8601
     */
    private String lastUpdated;

    private String status;
}
