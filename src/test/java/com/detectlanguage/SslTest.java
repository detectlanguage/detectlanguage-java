package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.*;

public class SslTest extends BaseTest {
    @Before
    public void enableSsl() {
        DetectLanguage.ssl = true;
    }

    @After
    public void disableSsl() {
        DetectLanguage.ssl = false;
    }

    @Test
    public void testSimpleDetect() throws APIError {
        String language = DetectLanguage.simpleDetect("Hello world");

        assertEquals(language, "en");
    }
}
