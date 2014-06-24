package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.BatchDetectResponse;
import com.detectlanguage.responses.DetectResponse;
import com.detectlanguage.responses.StatusResponse;

import java.util.HashMap;
import java.util.List;

public abstract class DetectLanguage {
    public static final Client CLIENT = new Client();

    public static String apiBase = "http://ws.detectlanguage.com/0.2/";
    public static String apiKey;
    public static int timeout = 3 * 1000;

    public static String simpleDetect(final String text) throws APIError {
        List<Result> results = detect(text);

        if (results.isEmpty())
            return null;
        else
            return results.get(0).language;
    }

    public static List<Result> detect(final String text) throws APIError {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("q", text);

        DetectResponse response = CLIENT.execute("detect", params,
                DetectResponse.class);

        return response.data.detections;
    }

    public static List<List<Result>> detect(final String[] texts)
            throws APIError {
        HashMap<String, String> params = new HashMap<String, String>();

        for (int i = 0; i < texts.length; i++) {
            params.put("q[" + i + "]", texts[i]);
        }

        BatchDetectResponse response = CLIENT.execute("detect", params,
                BatchDetectResponse.class);

        return response.data.detections;
    }

    public static StatusResponse getStatus() throws APIError {
        HashMap<String, String> params = new HashMap<String, String>();

        StatusResponse response = CLIENT.execute("user/status", params,
                StatusResponse.class);

        return response;
    }
}