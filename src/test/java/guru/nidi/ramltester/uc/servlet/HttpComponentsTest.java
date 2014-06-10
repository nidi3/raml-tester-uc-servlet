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
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.httpcomponents.RamlHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 */
public class HttpComponentsTest extends ServerTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private RamlDefinition api;

    @Before
    public void init() {
        api = RamlLoaders
                .fromClasspath(getClass())
                .load("api.yaml")
                .assumingBaseUri("http://guru.nidi/raml/simple/v1");
    }

    @Test
    public void testRequestAndResponse() throws IOException {
        final RamlHttpClient client = api.createHttpClient();

        send(client, "http://localhost:8081/greetings?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd&param=bla");
    }

    @Test
    public void testRequestOnly() throws IOException {
        final RamlHttpClient client = api.createHttpClient().notSending();

        send(client, "http://localhost:8081/greetings?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd&param=bla");
    }

    private void send(RamlHttpClient client, String request) throws IOException {
        final HttpGet get = new HttpGet(request);
        log.info("Send:        " + request);
        final HttpResponse response = client.execute(get);
        log.info("Result:      " + response.getStatusLine() + (response.getEntity() == null ? "" : EntityUtils.toString(response.getEntity())));
        log.info("Raml report: " + client.getLastReport());
    }
}
