package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.ErrorData;
import com.detectlanguage.responses.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Client {

    public static final String CHARSET = "UTF-8";

    private static final String AGENT = "detectlanguage-java";

    private static final RequestConfig requestConfig = RequestConfig
            .custom()
            .setSocketTimeout(DetectLanguage.timeout)
            .setConnectTimeout(DetectLanguage.timeout)
            .build();

    public Client() {
    }

    public <T> T execute(String method, Map<String, String> params,
                         Class<T> responseClass) throws APIError {
        Map<String, String> requestParams = new HashMap<String, String>(params);
        requestParams.put("key", DetectLanguage.apiKey);

        URI uri = buildUri(method);
        HttpPost request = new HttpPost(uri);
        request.setConfig(requestConfig);
        request.setEntity(buildPostParams(requestParams));
        addHeaders(request);

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            try {
                HttpResponse response = httpClient.execute(request);
                String body = EntityUtils.toString(response.getEntity());
                return processResponse(responseClass, body);
            } finally {
                httpClient.close();
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
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

    private URI buildUri(String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(DetectLanguage.apiBase);
        sb.append(path);
        if (params != null && params.size() > 0) {
            sb.append("?");
            sb.append(buildQueryString(params));
        }
        try {
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URI buildUri(String path) {
        return buildUri(path, null);
    }

    private void addHeaders(HttpUriRequest request) {
        String version = getClass().getPackage().getImplementationVersion();
        request.addHeader(new BasicHeader("User-Agent", AGENT + '/' + version));
        request.addHeader(new BasicHeader("Accept", "application/json"));
        request.addHeader(new BasicHeader("Accept-Charset", CHARSET));
    }

    private String buildQueryString(Map<String, String> params) {
        ArrayList<NameValuePair> nvs = new ArrayList<NameValuePair>(
                params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            NameValuePair nv = new BasicNameValuePair(entry.getKey(),
                    entry.getValue());
            nvs.add(nv);
        }
        String queryString = URLEncodedUtils.format(nvs, CHARSET);
        return queryString;
    }

    private UrlEncodedFormEntity buildPostParams(Map<String, String> map) {
        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Collection) {
                Collection<?> values = (Collection<?>) value;
                for (Object v : values) {
                    // This will add a parameter for each value in the
                    // Collection/List
                    parameters.add(new BasicNameValuePair(entry.getKey(),
                            v == null ? null : String.valueOf(v)));
                }
            } else {
                parameters.add(new BasicNameValuePair(entry.getKey(),
                        value == null ? null : String.valueOf(value)));
            }
        }

        try {
            return new UrlEncodedFormEntity(parameters, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
