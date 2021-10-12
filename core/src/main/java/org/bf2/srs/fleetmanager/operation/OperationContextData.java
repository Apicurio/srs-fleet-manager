package org.bf2.srs.fleetmanager.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_EVENT_TRACE_DATA;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_OPERATION_ID;

/**
 * Contains the inner state of a {@see org.bf2.srs.fleetmanager.operation.OperationContext}.
 * <p>
 * WARNING: This class and its contents MUST be serializable (and deserializable) to JSON using ObjectMapper.
 * When performing modifications, make sure previous values remain deserializable or are otherwise handled.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OperationContextData {

    @JsonProperty(KEY_OPERATION_ID)
    private String operationId;

    @JsonProperty(KEY_EVENT_TRACE_DATA)
    Map<String, String> traceData = new HashMap<>();
}
