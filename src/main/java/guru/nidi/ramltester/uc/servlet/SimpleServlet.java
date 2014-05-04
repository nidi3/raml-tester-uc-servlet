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
