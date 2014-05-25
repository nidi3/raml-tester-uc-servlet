/*
 * Copyright (C) ${project.inceptionYear} Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.ramltester.uc.servlet;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlTester;
import guru.nidi.ramltester.core.RamlReport;
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
        api = RamlTester
                .fromClasspath(getClass())
                .load("api.yaml")
                .assumingServletUri("http://guru.nidi/raml/simple/v1");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final RamlReport report = api.testAgainst(request, response, chain);
        log.info("Raml report: " + report);
    }

    @Override
    public void destroy() {

    }
}
