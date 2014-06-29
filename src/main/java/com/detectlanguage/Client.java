package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.ErrorData;
import com.detectlanguage.responses.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class Client {

    public static final String CHARSET = "UTF-8";

    private static final String AGENT = "detectlanguage-java";

    public Client() {
    }

    public <T> T execute(String method, Map<String, Object> params,
                         Class<T> responseClass) throws APIError {
        Map<String, Object> requestParams = new HashMap<String, Object>(params);

        requestParams.put("key", DetectLanguage.apiKey);

        URL url = buildUrl(method);
        String query = buildQuery(requestParams);

        try {
            HttpURLConnection conn = createPostConnection(url, query);

            try {
                // trigger the request
                int rCode = conn.getResponseCode();
                String body;

                if (rCode >= 200 && rCode < 300) {
                    body = getResponseBody(conn.getInputStream());
                } else {
                    body = getResponseBody(conn.getErrorStream());
                }

                return processResponse(responseClass, body);
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T processResponse(Class<T> responseClass, String body)
            throws APIError {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        if (body.contains("\"error\":")) {
            ErrorResponse errorResponse = gson.fromJson(body,
                    ErrorResponse.class);
            ErrorData error = errorResponse.error;
            throw new APIError(error.message, error.code);
        }

        return gson.fromJson(body, responseClass);
    }

    private URL buildUrl(String path, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(DetectLanguage.apiBase);
        sb.append(path);
        if (params != null && params.size() > 0) {
            sb.append("?");
            sb.append(buildQuery(params));
        }
        try {
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL buildUrl(String path) {
        return buildUrl(path, null);
    }

    private HttpURLConnection createPostConnection(
            URL url, String query) throws IOException {
        HttpURLConnection conn = createConnection(url);

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", String.format(
                "application/x-www-form-urlencoded;charset=%s", CHARSET));

        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(query.getBytes(CHARSET));
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return conn;
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(DetectLanguage.timeout);
        conn.setReadTimeout(DetectLanguage.timeout);
        conn.setUseCaches(false);

        String version = getClass().getPackage().getImplementationVersion();

        conn.setRequestProperty("User-Agent", AGENT + '/' + version);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Accept-Charset", CHARSET);

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
