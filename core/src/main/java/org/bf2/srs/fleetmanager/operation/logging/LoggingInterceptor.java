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

package org.bf2.srs.fleetmanager.operation.logging;

import org.bf2.srs.fleetmanager.App;
import org.bf2.srs.fleetmanager.common.operation.InterceptorPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author eric.wittmann@gmail.com
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Logged
@Interceptor
@Priority(InterceptorPriority.LOGGING)
public class LoggingInterceptor {

    private static final Map<Class<?>, Logger> loggers = new HashMap<>();

    @AroundInvoke
    public Object logMethodEntry(InvocationContext context) throws Exception {
        Logger logger = null;
        try {
            Class<?> targetClass = App.class;
            Object target = context.getTarget();
            if (target != null) {
                targetClass = target.getClass();
            }

            logger = getLogger(targetClass);
        } catch (Throwable t) {
        }

        StringBuilder info = logEnter(context, logger);
        Object rval = context.proceed();
        logLeave(logger, info, rval);
        return rval;
    }

    private StringBuilder logEnter(InvocationContext context, Logger logger) {
        if (context != null && context.getMethod() != null && logger != null) {
            String name = context.getMethod().getName();
            Parameter[] pns = context.getMethod().getParameters();
            Object[] pvs = context.getParameters();
            if (pns.length != pvs.length) { // Just in case
                logger.debug("Cannot log entering method for {}", name);
                return new StringBuilder("?");
            }
            StringBuilder info = new StringBuilder();
            info.append(name).append("(");
            for (int i = 0, len = pns.length; i < len; i++) {
                info.append(pns[i].getName()).append(" = ").append(pvs[i]);
                if (i != len - 1)
                    info.append(",");
                info.append(")");
            }
            logger.debug("ENTERING method {}", info);
            return info;
        }
        return new StringBuilder("?");
    }

    private void logLeave(Logger logger, StringBuilder info, Object rval) {
        if (logger != null)
            logger.debug("LEAVING method {}", info.append(" returning ").append(rval));
    }

    /**
     * Gets a logger for the given target class.
     */
    private Logger getLogger(Class<?> targetClass) {
        return loggers.computeIfAbsent(targetClass, k -> LoggerFactory.getLogger(targetClass));
    }
}
