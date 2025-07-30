package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.StatusResponse;

import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public abstract class DetectLanguage {
    public static String apiHost = "ws.detectlanguage.com";
    public static String apiVersion = "v3";
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
        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("q", text);

        Gson gson = new Gson();
        String payload = gson.toJson(jsonMap);

        Type resultType = new TypeToken<List<Result>>(){}.getType();

        return getClient().post("detect", payload, resultType);
    }

    public static List<List<Result>> detect(final String[] texts)
            throws APIError {
        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("q", texts);

        Gson gson = new Gson();
        String payload = gson.toJson(jsonMap);

        Type resultType = new TypeToken<List<List<Result>>>(){}.getType();

        return getClient().post("detect-batch", payload, resultType);
    }

    public static StatusResponse getStatus() throws APIError {
        return getClient().get("account/status", StatusResponse.class);
    }

    public static List<LanguageInfo> getLanguages() throws APIError {
        Type resultType = new TypeToken<List<LanguageInfo>>(){}.getType();

        return getClient().get("languages", resultType);
    }

    private static Client getClient() {
        return new Client();
    }
}
