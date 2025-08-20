package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.ErrorData;
import com.detectlanguage.responses.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.lang.reflect.Type;

public class Client {
    private static final String AGENT = "detectlanguage-java";
    private static final String CHARSET = "UTF-8";

    public Client() {
    }

    public <T> T get(String path, Type responseType) throws APIError {
        return execute("GET", path, null, null, responseType);
    }

    public <T> T post(String path, String payload, Type responseType) throws APIError {
        return execute("POST", path, null, payload, responseType);
    }

    private <T> T execute(String method, String path, Map<String, Object> params,
    String payload, Type responseType) throws APIError {
        URL url = buildUrl(path, params);

        try {
            HttpURLConnection conn = createConnection(url);

            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");

            if (payload != null) {
                OutputStream output = null;
                try {
                    output = conn.getOutputStream();
                    output.write(payload.getBytes(CHARSET));
                } finally {
                    if (output != null) {
                        output.close();
                    }
                }
            }

            try {
                // trigger the request
                int rCode = conn.getResponseCode();
                String body;

                if (rCode >= 200 && rCode < 300) {
                    body = getResponseBody(conn.getInputStream());
                } else {
                    body = getResponseBody(conn.getErrorStream());
                }

                return processResponse(responseType, body);
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T processResponse(Type responseType, String body)
            throws APIError {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        if (body.contains("\"error\":")) {
            ErrorResponse errorResponse = gson.fromJson(body,
                    ErrorResponse.class);
            ErrorData error = errorResponse.error;
            throw new APIError(error.message, error.code);
        }

        try {
            return gson.fromJson(body, responseType);
        } catch (JsonSyntaxException e) {
            throw new APIError("Server error. Invalid response format.", 9999);
        }
    }

    private URL buildUrl(String path, Map<String, Object> params) {
        String url = String.format(
                "https://%s/%s/%s",
                DetectLanguage.apiHost,
                DetectLanguage.apiVersion,
                path);


        if (params != null && params.size() > 0)
            url+= '?' + buildQuery(params);

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(DetectLanguage.timeout);
        conn.setReadTimeout(DetectLanguage.timeout);
        conn.setUseCaches(false);

        String version = getClass().getPackage().getImplementationVersion();

        conn.setRequestProperty("User-Agent", AGENT + '/' + version);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + DetectLanguage.apiKey);

        return conn;
    }

    private static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String urlEncodePair(String k, String v) {
        return String.format("%s=%s", urlEncode(k), urlEncode(v));
    }

    private static String buildQuery(Map<String, Object> params) {
        Map<String, String> flatParams = flattenParams(params);
        StringBuilder queryStringBuffer = new StringBuilder();
        for (Map.Entry<String, String> entry : flatParams.entrySet()) {
            if (queryStringBuffer.length() > 0) {
                queryStringBuffer.append("&");
            }
            queryStringBuffer.append(urlEncodePair(entry.getKey(),
                    entry.getValue()));
        }
        return queryStringBuffer.toString();
    }

    private static Map<String, String> flattenParams(Map<String, Object> params) {
        if (params == null) {
            return new HashMap<String, String>();
        }
        Map<String, String> flatParams = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                Map<String, Object> flatNestedMap = new HashMap<String, Object>();
                Map<?, ?> nestedMap = (Map<?, ?>) value;
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    flatNestedMap.put(
                            String.format("%s[%s]", key, nestedEntry.getKey()),
                            nestedEntry.getValue());
                }
                flatParams.putAll(flattenParams(flatNestedMap));
            } else if (value == null) {
                flatParams.put(key, "");
            } else if (value != null) {
                flatParams.put(key, value.toString());
            }
        }
        return flatParams;
    }

    private static String getResponseBody(InputStream responseStream)
            throws IOException {
        //\A is the beginning of
        // the stream boundary
        String rBody = new Scanner(responseStream, CHARSET)
                .useDelimiter("\\A")
                .next(); //

        responseStream.close();
        return rBody;
    }
}
