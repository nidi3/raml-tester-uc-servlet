package guru.nidi.ramltester.uc.servlet;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlViolations;
import guru.nidi.ramltester.servlet.RamlTesters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 *
 */
public class RamlFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private RamlDefinition api;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        api = RamlDefinition.fromClasspath(getClass(), "api.yaml");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final RamlViolations violations = RamlTesters.executeFilterChain(api, request, response, chain);
        log.info("Violations: " + violations);
    }

    @Override
    public void destroy() {

    }
}
