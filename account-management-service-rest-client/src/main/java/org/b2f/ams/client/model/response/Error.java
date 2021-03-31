package org.b2f.ams.client.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static lombok.AccessLevel.PACKAGE;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "href",
        "id",
        "kind",
        "code",
        "operationId",
        "reason"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Error {

    private String href;
    private String id;
    private String kind;
    private String code;
    private String operationId;
    private String reason;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
