package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import org.junit.Test;
import org.junit.Ignore;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Copyright 2014 Getty Images
 * User: dbabichev
 * Date: 6/23/2014
 * Time: 12:41 PM
 */
public class MultithreadedTest extends BaseTest {
    public static final String[] SAMPLES = {"Labas rytas", "Hello world", "Dolce far niente"};
    public static final String[] SAMPLE_CODES = {"lt", "en", "it"};

    public static int TEST_THREADS = 10;

    @Ignore("fails locally because of connection timeouts")
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
            assertEquals(thread.expectedLanguage, thread.detectedLanguage);
        }
    }

    static class RequestThread extends Thread {

        public String detectedLanguage;
        public String expectedLanguage;

        @Override
        public void run() {
            try {
                int n = (new Random()).nextInt(SAMPLES.length);
                expectedLanguage = SAMPLE_CODES[n];
                sleep((new Random()).nextInt(10000));
                detectedLanguage = DetectLanguage.simpleDetect(SAMPLES[n]);
            } catch (InterruptedException e) {
            } catch (APIError apiError) {
                apiError.printStackTrace();
            }
        }
    }
}
