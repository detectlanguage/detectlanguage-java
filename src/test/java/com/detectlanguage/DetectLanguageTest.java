package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.AccountStatusResponse;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class DetectLanguageTest extends BaseTest {

    @Test
    public void testDetectCode() throws APIError {
        String language = DetectLanguage.detectCode("Hello world");

        assertEquals(language, "en");
    }

    @Test
    public void testDetect() throws APIError {
        List<Result> results = DetectLanguage.detect("Hello world");

        Result result = results.get(0);

        assertEquals(result.language, "en");
        assertTrue(result.score > 0);
    }

    @Test(expected = APIError.class)
    public void testDetectError() throws APIError {
        DetectLanguage.apiKey = "INVALID";
        DetectLanguage.detect("Hello world");
    }

    @Test
    public void testBatchDetect() throws APIError {
        String[] texts = {"Hello world", "Kabo kabikas, žiūri žiūrikas"};

        List<List<Result>> results = DetectLanguage.detect(texts);
        Result result;

        result = results.get(0).get(0);

        assertEquals(result.language, "en");
        assertTrue(result.score > 0);

        result = results.get(1).get(0);

        assertEquals(result.language, "lt");
        assertTrue(result.score > 0);
    }

    @Test(expected = APIError.class)
    public void testBatchDetectError() throws APIError {
        DetectLanguage.apiKey = "INVALID";

        String[] texts = {"Hello world", "Kabo kabikas, žiūri žiūrikas"};

        DetectLanguage.detect(texts);
    }

    @Test
    public void testGetAccountStatus() throws APIError {
        AccountStatusResponse statusResponse = DetectLanguage.getAccountStatus();

        assertThat(statusResponse.getDate(), is(instanceOf(Date.class)));
        assertTrue(statusResponse.getRequests() >= 0);
        assertTrue(statusResponse.getBytes() >= 0);
        assertThat(statusResponse.getPlan(), is(instanceOf(String.class)));
        assertThat(statusResponse.getPlanExpires(),
                anyOf(nullValue(), instanceOf(Date.class)));
        assertTrue(statusResponse.getDailyRequestsLimit() > 0);
        assertTrue(statusResponse.getDailyBytesLimit() > 0);
        assertEquals(statusResponse.getStatus(), "ACTIVE");
    }

    @Test(expected = APIError.class)
    public void testStatusError() throws APIError {
        DetectLanguage.apiKey = "INVALID";
        DetectLanguage.getAccountStatus();
    }

    @Test
    public void testGetLanguages() throws APIError {
        List<LanguageInfo> languages = DetectLanguage.getLanguages();

        assertTrue(languages.size() > 0);
        assertTrue(languages.get(0).code.length() > 0);
        assertTrue(languages.get(0).name.length() > 0);
    }
}
