package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.BatchDetectResponse;
import com.detectlanguage.responses.DetectResponse;
import com.detectlanguage.responses.StatusResponse;

import java.util.HashMap;
import java.util.List;

public abstract class DetectLanguage {
    public static String apiHost = "ws.detectlanguage.com";
    public static String apiVersion = "0.2";
    public static String apiKey;
    public static int timeout = 3 * 1000;
    public static boolean ssl = false;

    public static String simpleDetect(final String text) throws APIError {
        List<Result> results = detect(text);

        if (results.isEmpty())
            return null;
        else
            return results.get(0).language;
    }

    public static List<Result> detect(final String text) throws APIError {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("q", text);

        DetectResponse response = getClient().execute("detect", params,
                DetectResponse.class);

        return response.data.detections;
    }

    public static List<List<Result>> detect(final String[] texts)
            throws APIError {
        HashMap<String, Object> params = new HashMap<String, Object>();

        for (int i = 0; i < texts.length; i++) {
            params.put("q[" + i + "]", texts[i]);
        }

        BatchDetectResponse response = getClient().execute("detect", params,
                BatchDetectResponse.class);

        return response.data.detections;
    }

    public static StatusResponse getStatus() throws APIError {
        HashMap<String, Object> params = new HashMap<String, Object>();

        StatusResponse response = getClient().execute("user/status", params,
                StatusResponse.class);

        return response;
    }

    private static Client getClient() {
        return new Client();
    }
}