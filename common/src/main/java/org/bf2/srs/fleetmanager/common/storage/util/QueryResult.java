package org.bf2.srs.fleetmanager.common.storage.util;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class QueryResult<T> {

    List<T> items;

    long count;
}
