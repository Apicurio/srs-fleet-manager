package org.bf2.srs.fleetmanager.operation.auditing.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bf2.srs.fleetmanager.common.SerDesObjectMapperProducer;
import org.bf2.srs.fleetmanager.operation.OperationContext;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingEvent;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_EVENT_TRACE_DATA;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_OPERATION_ID;

/**
 * MUST be thread safe.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class AuditingServiceImpl implements AuditingService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditingServiceImpl.class);

    public static final ObjectMapper MAPPER = SerDesObjectMapperProducer.getMapper();

    private static final Marker AUDIT_LOG_MARKER = MarkerFactory.getMarker("audit");

    @Inject
    OperationContext opCtx;

    private void init() {
        if (opCtx.getContextData().getTraceData() == null) {
            opCtx.getContextData().setTraceData(new HashMap<>());
        }

        opCtx.getContextData().getTraceData().putIfAbsent(KEY_OPERATION_ID, opCtx.getOperationId());

        if (opCtx.getTraceDataJson() == null) {
            opCtx.setTraceDataJson(MAPPER.valueToTree(opCtx.getContextData().getTraceData()));
        }
    }

    @Override
    public void addTraceMetadata(String key, Object value, boolean overwrite) {
        init();
        if (value == null) {
            LOG.debug("No value provided for audit trace data key '{}'. Operation ID is {}.", key, opCtx.getOperationId());
            return;
        }
        var traceData = opCtx.getContextData().getTraceData();
        if (overwrite || !traceData.containsKey(key)) {
            traceData.put(key, String.valueOf(value));
            opCtx.setTraceDataJson(MAPPER.valueToTree(traceData));
        }
    }

    @Override
    public void recordEvent(AuditingEvent event) {
        init();
        var json = (ObjectNode) MAPPER.valueToTree(event);
        json.put("type", "audit");
        json.set(KEY_EVENT_TRACE_DATA, opCtx.getTraceDataJson());
        LOG.info(AUDIT_LOG_MARKER, "{}", json);
    }

    /**
     * Use for recording auditing events when the Request Context is not available.
     */
    static void recordEventNoContext(AuditingEvent event) {
        // Without trace data (including operation ID).
        var json = (ObjectNode) MAPPER.valueToTree(event);
        json.put("type", "audit");
        LOG.info(AUDIT_LOG_MARKER, "{}", json);
    }
}
