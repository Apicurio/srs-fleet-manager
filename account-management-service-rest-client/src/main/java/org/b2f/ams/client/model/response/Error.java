package org.b2f.ams.client.model.response;

public class Error {

    private String href;
    private String id;
    private String kind;
    private String code;
    private String operationId;
    private String reason;

    public Error() {
    }

    public Error(String href, String id, String kind, String code, String operationId, String reason) {
        this.href = href;
        this.id = id;
        this.kind = kind;
        this.code = code;
        this.operationId = operationId;
        this.reason = reason;
    }

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
