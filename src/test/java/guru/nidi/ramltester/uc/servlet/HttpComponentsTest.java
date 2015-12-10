/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
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

import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.SimpleReportAggregator;
import guru.nidi.ramltester.core.Usage;
import guru.nidi.ramltester.core.UsageItem;
import guru.nidi.ramltester.httpcomponents.RamlHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;

import static guru.nidi.ramltester.core.UsageItem.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class HttpComponentsTest extends ServerTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static RamlHttpClient baseClient = RamlLoaders
            .fromClasspath(HttpComponentsTest.class)
            .load("api.raml")
            .assumingBaseUri("http://guru.nidi/raml/simple/v1")
            .createHttpClient();

    @Test
    public void testRequestAndResponse() throws IOException {
        final SimpleReportAggregator aggregator = new SimpleReportAggregator();
        final RamlHttpClient client = baseClient.aggregating(aggregator);

        send(client, "http://localhost:8081/greetings?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd&param=bla");

        assertUsage(aggregator.getUsage(), EnumSet.allOf(UsageItem.class));
    }

    @Test
    public void testRequestOnly() throws IOException {
        final SimpleReportAggregator aggregator = new SimpleReportAggregator();
        final RamlHttpClient client = baseClient.notSending().aggregating(aggregator);

        send(client, "http://localhost:8081/greetings?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd");
        send(client, "http://localhost:8081/greeting?nofilter&name=ddd&param=bla");

        assertUsage(aggregator.getUsage(), EnumSet.of(RESOURCE, ACTION, QUERY_PARAMETER));
    }

    private void send(RamlHttpClient client, String request) throws IOException {
        final HttpGet get = new HttpGet(request);
        log.info("Send:        " + request);
        final HttpResponse response = client.execute(get);
        log.info("Result:      " + response.getStatusLine() + (response.getEntity() == null ? "" : EntityUtils.toString(response.getEntity())));
        log.info("Raml report: " + client.getLastReport());
    }

    private void assertUsage(Usage usage, EnumSet<UsageItem> usageItems) {
        for (UsageItem usageItem : usageItems) {
            assertEquals(usageItem.name(), 0, usageItem.get(usage).size());
        }
    }

}
