package org.bf2.srs.fleetmanager.common.errors;

/**
 * This interface MUST be implemented by Exceptions (for now),
 * that are able to provide user-friendly error information.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface UserError {

    UserErrorInfo getUserErrorInfo();
}
