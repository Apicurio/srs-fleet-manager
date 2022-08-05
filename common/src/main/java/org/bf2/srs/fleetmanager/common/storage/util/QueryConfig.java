package org.bf2.srs.fleetmanager.common.storage.util;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class QueryConfig {

    public static final int DEFAULT_PAGE_SIZE = 10;

    public enum Direction {
        /**
         * Sort in ascending order (the default).
         */
        ASCENDING,
        /**
         * Sort in descending order (opposite from the default).
         */
        DESCENDING;
    }

    private int index;

    private int size = DEFAULT_PAGE_SIZE;

    private Direction sortDirection = Direction.ASCENDING;

    @NotEmpty
    private String sortColumn;
}
