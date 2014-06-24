package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import org.apache.http.pool.PoolStats;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Copyright 2014 Getty Images
 * User: dbabichev
 * Date: 6/23/2014
 * Time: 12:41 PM
 */
public class DetectLanguageMultithreadedTest extends BaseTest {

    public static final String CAPTION = "The PoolingClientConnectionManager will allocate connections based on its " +
            "configuration. If all connections for a given route have already been leased, a request for a connection" +
            " will block until a connection is released back to the pool. One can ensure the connection manager does" +
            " not block indefinitely in the connection request operation by setting 'http.conn-manager.timeout' to a" +
            " positive value. If the connection request cannot be serviced within the given time period " +
            "ConnectionPoolTimeoutException will be thrown.";

    public static int TEST_THREADS = 10;

//    @AfterClass
//    public static void shutdownPoolingManager() {
//        DetectLanguage.CLIENT.shutdown();
//    }

    @Test
    public void multithreadedRequestExecution() throws InterruptedException {

        // create a thread for each request
        RequestThread[] threads = new RequestThread[TEST_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new RequestThread();
        }

        // start the threads
        for (RequestThread thread : threads) {
            thread.start();
        }

        // join the threads
        for (RequestThread thread : threads) {
            thread.join();
        }

        for (RequestThread thread : threads) {
            assertEquals("en", thread.detectedLanguage);
        }

        assertConnections();
    }

    private static void assertConnections() {
        PoolStats statistics = DetectLanguage.CLIENT.getStatistics();
        assertEquals(0, statistics.getLeased());
        assertEquals(TEST_THREADS, statistics.getAvailable());
        assertEquals(Client.MAX_TOTAL_CONNECTIONS, statistics.getMax());
    }

    static class RequestThread extends Thread {

        public String detectedLanguage;

        @Override
        public void run() {
            try {
                detectedLanguage = DetectLanguage.simpleDetect(CAPTION);
            } catch (APIError apiError) {
                apiError.printStackTrace();
            }
        }

    }
}
