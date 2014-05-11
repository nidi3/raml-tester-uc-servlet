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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class SimpleServlet extends HttpServlet {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        if (!req.getPathInfo().equals("/greeting")) {
            resp.sendError(404);
            return;
        }

        final PrintWriter out = resp.getWriter();
        String name = req.getParameter("name");
        if (name == null) {
            name = "World";
        }
        out.print("{\"id\":" + counter.incrementAndGet() + ",\"content\":\"" + String.format(template, name) + "\"}");
        out.flush();
    }
}
