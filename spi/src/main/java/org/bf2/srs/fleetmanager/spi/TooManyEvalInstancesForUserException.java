/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

/**
 * @author eric.wittmann@gmail.com
 */
public class TooManyEvalInstancesForUserException extends Exception implements UserError {

    private static final long serialVersionUID = -4082728585921201323L;

    public TooManyEvalInstancesForUserException() {
        super();
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_EVAL_INSTANCES_EXCEEDED);
    }
}
