package com.detectlanguage;

import org.junit.Before;

public class BaseTest {
    @Before
    public void setUp() {
        DetectLanguage.apiHost = System.getProperty("detectlanguage_api_host",
                DetectLanguage.apiHost);
        DetectLanguage.apiKey = System.getProperty("detectlanguage_api_key",
                System.getenv().get("DETECTLANGUAGE_API_KEY"));
    }
}
