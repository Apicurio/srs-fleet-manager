package org.bf2.srs.fleetmanager.common.operation.auditing;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface AuditingConstants {

    String EVENT_ID_METHOD_CALL_PREFIX = "method_call_";

    String KEY_EVENT_ID = "event_id";
    String KEY_EVENT_DESCRIPTION = "description";
    String KEY_EVENT_TRACE_DATA = "trace_data";
    String KEY_EVENT_DATA = "data";
    String KEY_EVENT_SUCCESS = "event_success";

    String KEY_OPERATION_ID = "operation_id";

    String KEY_CLASS = "class";

    String KEY_REQUEST_SOURCE_IP = "request_source_ip";
    String KEY_REQUEST_FORWARDED_FOR = "request_x_forwarded_for";
    String KEY_REQUEST_METHOD = "request_method";
    String KEY_REQUEST_PATH = "request_path";

    String KEY_RESPONSE_CODE = "response_code";

    String KEY_USER_ACCOUNT_ID = "user_account_id";
    String KEY_USER_ACCOUNT_NAME = "user_account_name";
    String KEY_USER_ORG_ID = "user_org_id";
    String KEY_USER_IS_ORG_ADMIN = "user_is_org_admin";

    String KEY_REGISTRY_ID = "registry_id";
    String KEY_REGISTRY_NAME = "registry_name";
    String KEY_REGISTRY_INSTANCE_TYPE = "registry_instance_type";
    String KEY_REGISTRY_ORG_ID = "registry_org_id";
    String KEY_REGISTRY_OWNER = "registry_owner";
    String KEY_REGISTRY_OWNER_ID = "registry_owner_id";
    String KEY_REGISTRY_SUBSCRIPTION_ID = "registry_subscription_id";

    String KEY_DEPLOYMENT_ID = "deployment_id";
    String KEY_DEPLOYMENT_URL = "deployment_url";

    String KEY_TENANT_MANAGER_URL = "tenant_manager_url";

    String KEY_TENANT_ID = "tenant_id";
    String KEY_TENANT_ORG_ID = "tenant_org_id";
    String KEY_TENANT_USER = "tenant_org_id";

    String KEY_AMS_RESOURCE_TYPE = "ams_resource_type";
    String KEY_AMS_SUBSCRIPTION_ID = "ams_subscription_id";

    String KEY_ERROR_MESSAGE = "error_message";

    String KEY_USER_ERROR_ID = "user_error_id";

    String KEY_TASK_ID = "task_id";
}
