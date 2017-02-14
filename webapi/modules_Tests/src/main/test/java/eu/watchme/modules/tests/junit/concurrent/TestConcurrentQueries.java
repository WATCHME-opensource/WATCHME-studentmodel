/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 31/7/2015
 * Copyright: Copyright (C) 2014-2017 WATCHME Consortium
 * License: The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.watchme.modules.tests.junit.concurrent;

import eu.watchme.modules.domainmodel.jit.FeedbackResponseModel;
import eu.watchme.modules.tests.sm.client.SmTestClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static eu.watchme.modules.tests.sm.client.TestHelper.asString;

public class TestConcurrentQueries {
    private static SmTestClient client;

    @BeforeClass
    public static void setupTest() {
        client = new SmTestClient();
        client.initClient(URI.create("http://localhost:8081/sm/api/epass"),
                URI.create("http://localhost:8081/sm/api/jit"));
    }

    @Test
    public void testQuery2() {
        try {
            test(2);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuery4() {
        try {
            test(4);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void test(final int threadCount) throws InterruptedException, ExecutionException {
        List<Callable<Boolean>> tasks = new ArrayList<>(threadCount);
        for (long i = 0; i < threadCount; i++) {
            tasks.add(new QueryExecutor("query" + i));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Boolean>> futures = executorService.invokeAll(tasks);
        List<Boolean> resultList = new ArrayList<>(futures.size());

        for (Future<Boolean> future : futures) {
            resultList.add(future.get());
        }

        Assert.assertEquals(threadCount, futures.size());

        List<Boolean> expectedList = new ArrayList<>(threadCount);
        for (long i = 0; i < threadCount; i++) {
            expectedList.add(true);
        }
        Assert.assertEquals(expectedList, resultList);
    }

    private static class QueryExecutor implements Callable<Boolean> {
        private String queryName;

        public QueryExecutor(String queryName) {
            this.queryName = queryName;
        }

        @Override
        public Boolean call() throws Exception {
            long timestamp = System.currentTimeMillis();
            System.out.printf("[%s] Query started\n", queryName);

            FeedbackResponseModel responseModel = client.queryJitData(TestMockup.createDummyFeedbackQueryData());
            System.out.printf("[%s] JIT query: %s\n", queryName, asString(responseModel));
            System.out.printf("[%s] Time spent for the query: %s\n", queryName,
                    (System.currentTimeMillis() - timestamp));

            return responseModel != null && responseModel.getFeedbackForEpas() != null &&
                    !responseModel.getFeedbackForEpas().isEmpty();
        }
    }
}
