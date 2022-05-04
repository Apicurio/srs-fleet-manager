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

package org.bf2.srs.fleetmanager.common.operation.auditing;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Apply on a method in order to include its call in the auditing log.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Audited {

    /**
     * If empty, the method name will be used to generate the event identifier.
     */
    @Nonbinding
    String eventId() default "";

    /**
     * If empty, auditing event description will be omitted.
     */
    @Nonbinding
    String eventDescription() default "";

    /**
     * If a method parameter value should be recorded to the auditing log,
     * but there is no extractor defined (e.g. the value is a type without specific meaning, such as a String),
     * this field can be used by adding two successive values:
     *
     * 1. Position of the given parameter, starting at 0, as String. Parameter name is not used,
     *    in case it is not available via reflection.
     * 2. Key under which the value of the parameter should be recorded.
     *
     * There can be more than one such pair.
     */
    @Nonbinding
    String[] extractParameters() default {};

    /**
     * If a method return value should be recorded to the auditing log,
     * but there is no extractor defined (e.g. the value is a type without specific meaning, such as a String),
     * this field can be used to define a key under which the return value should be recorded.
     */
    @Nonbinding
    String extractResult() default "";
}
