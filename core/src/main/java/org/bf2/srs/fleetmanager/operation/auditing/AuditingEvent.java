package org.bf2.srs.fleetmanager.operation.auditing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.*;

/**
 * This class represents an auditing event, i.e. a piece of audit data.
 * Does not usually contain auditing metadata for the whole trace, such as auth data or source IP address.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Getter
@EqualsAndHashCode
@ToString
public class AuditingEvent {

    private static final Logger LOG = LoggerFactory.getLogger(AuditingEvent.class);

    @Setter
    @JsonProperty(KEY_EVENT_ID)
    String eventId;

    @Setter
    @JsonProperty(KEY_EVENT_DESCRIPTION)
    String eventDescription;

    @JsonProperty(KEY_EVENT_DATA)
    Map<String, String> data;

    @Setter
    @JsonProperty(KEY_EVENT_SUCCESS)
    boolean successful;

    public AuditingEvent() {
        data = new HashMap<>();
    }

    public void addData(String key, Object value) {
        if (value == null) {
            LOG.debug("No value provided for audit data key '{}'.", key);
            return;
        }
        this.data.put(key, String.valueOf(value));
    }
}
