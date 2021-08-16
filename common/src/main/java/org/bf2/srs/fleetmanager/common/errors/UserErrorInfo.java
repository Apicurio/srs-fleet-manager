package org.bf2.srs.fleetmanager.common.errors;

import lombok.Getter;

import static java.util.Objects.requireNonNull;

/**
 * Contains user-friendly error information.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class UserErrorInfo {

    @Getter
    private final UserErrorCode code;

    @Getter
    private final Object[] reasonArgs;

    public static UserErrorInfo create(UserErrorCode code, Object... reasonArgs) {
        return new UserErrorInfo(code, reasonArgs);
    }

    public UserErrorInfo(UserErrorCode code, Object[] reasonArgs) {
        requireNonNull(code);
        requireNonNull(reasonArgs);
        this.code = code;
        this.reasonArgs = reasonArgs;
    }

    public String getReason() {
        if (reasonArgs.length != code.getReasonArgsCount()) {
            throw new IllegalArgumentException(
                    String.format("Could not format reason string for user error. Requires %s arguments, but got %s",
                            code.getReasonArgsCount(), reasonArgs.length));
        }
        return String.format(code.getReason(), reasonArgs);
    }
}
