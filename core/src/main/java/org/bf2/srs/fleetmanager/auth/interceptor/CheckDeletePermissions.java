package org.bf2.srs.fleetmanager.auth.interceptor;


import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Make sure the target method declares {@link org.bf2.srs.fleetmanager.auth.NotAuthorizedException}
 * in its throws clause, otherwise it'll be wrapped in
 * {@link io.quarkus.arc.ArcUndeclaredThrowableException}.
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface CheckDeletePermissions {
}
