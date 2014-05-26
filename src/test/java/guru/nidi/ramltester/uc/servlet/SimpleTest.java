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

import org.apache.catalina.*;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;

/**
 *
 */
public class SimpleTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static Tomcat tomcat;
    private static Server server;

    @BeforeClass
    public static void startTomcat() throws ServletException, LifecycleException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        tomcat = new Tomcat();
        tomcat.setPort(8081);
        tomcat.setBaseDir(".");
        Context ctx = tomcat.addWebapp("/", "src/main/webapp");
        ctx.setJarScanner(new JarScanner() {
            @Override
            public void scan(ServletContext context, ClassLoader classloader, JarScannerCallback callback, Set<String> jarsToSkip) {
            }
        });
        ((Host) ctx.getParent()).setAppBase("");
        tomcat.start();
        server = tomcat.getServer();
        server.start();
    }

    @AfterClass
    public static void stopTomcat() throws LifecycleException {
        if (tomcat.getServer() != null && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
            if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
                tomcat.stop();
            }
            tomcat.destroy();
        }
    }

    @Test
    public void request() throws IOException {
        send("http://localhost:8081/greetings");
        send("http://localhost:8081/greeting");
        send("http://localhost:8081/greeting?name=ddd");
        send("http://localhost:8081/greeting?name=ddd&param=bla");
    }

    private void send(String request) throws IOException {
        final CloseableHttpClient client = HttpClientBuilder.create().build();
        final HttpGet get = new HttpGet(request);
        log.info("Send:   " + request);
        final CloseableHttpResponse response = client.execute(get);
        log.info("Result: " + response.getStatusLine() + EntityUtils.toString(response.getEntity()));
    }
}
