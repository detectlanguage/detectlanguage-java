package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerErrorTest extends BaseTest {
    @Before
    public void setInvalidHost() {
        DetectLanguage.apiHost = "www.detectlanguage.com";
    }

    @After
    public void resetHost() {
        DetectLanguage.apiHost = "ws.detectlanguage.com";
    }

    @Test(expected = APIError.class)
    public void testSimpleDetect() throws APIError {
        DetectLanguage.simpleDetect("Hello world");
    }
}
