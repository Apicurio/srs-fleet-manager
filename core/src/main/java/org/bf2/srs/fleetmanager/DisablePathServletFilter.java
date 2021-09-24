package org.bf2.srs.fleetmanager;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Main Servlet filter for the REST APIs.
 * <p>
 * Can be used to disable a given path, e.g. to hide the "private" API in prod.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class DisablePathServletFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.rest-api.disabled-paths-patterns")
    Optional<String> disabledPathPatternsProperty;

    List<Pattern> disabledPathPatterns;

    @PostConstruct
    void init() {
        disabledPathPatterns = disabledPathPatternsProperty.map(prop ->
                Arrays.stream(prop.split(","))
                        .map(Pattern::compile)
                        .collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        if (requestURI != null) {

            if (isApiPathDisabled(requestURI)) {
                log.warn("Request {} is rejected because it matches a disabled API path", requestURI);
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.reset();
                httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return; // Return to stop the filter chain
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isApiPathDisabled(String requestURI) {
        return disabledPathPatterns.stream().anyMatch(p -> p.matcher(requestURI).matches());
    }
}
