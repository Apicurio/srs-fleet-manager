package org.bf2.srs.fleetmanager.operation;

import com.fasterxml.jackson.databind.JsonNode;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_OPERATION_ID;

/**
 * Operation Context represents metadata belonging to an execution of a single operation.
 * Operation is a logical sequence of computation, even asynchronous, currently
 * initiated by user's request to a given REST API endpoint.
 * <p>
 * It's implemented using a request scoped bean. The context must be activated
 * using {@see javax.enterprise.context.control.ActivateRequestContext} and the data
 * must be manually managed (loaded and persisted) at the scope boundaries.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@RequestScoped
public class OperationContext {

    private OperationContextData contextData = null;

    @Getter
    @Setter
    private JsonNode traceDataJson;

    public boolean isContextDataLoaded() {
        return contextData != null;
    }

    private void ensureContextDataIsLoaded() {
        if (!isContextDataLoaded())
            throw new IllegalStateException("Operation Context data is not loaded");
    }

    public void loadContextData(OperationContextData data) {
        Objects.requireNonNull(data);
        this.contextData = data;
        initSentry();
    }

    public void loadNewContextData() {
        contextData = new OperationContextData();
        contextData.setOperationId(UUID.randomUUID().toString());
        initSentry();
    }

    public OperationContextData getContextData() {
        ensureContextDataIsLoaded();
        return contextData;
    }

    public String getOperationId() {
        ensureContextDataIsLoaded();
        return this.contextData.getOperationId();
    }

    private void initSentry() {
        Sentry.getContext().addExtra(KEY_OPERATION_ID, this.getOperationId());
        Sentry.getContext().addTag(KEY_OPERATION_ID, this.getOperationId());
    }
}
