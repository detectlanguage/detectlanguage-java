package com.detectlanguage;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.ErrorData;
import com.detectlanguage.responses.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.pool.PoolStats;
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

    public static final int MAX_TOTAL_CONNECTIONS = 200;
    public static final int MAX_CONNECTIONS_PER_ROUTE = 100;

    private static final String AGENT = "detectlanguage-java";

    private final HttpClient httpClient;

    public Client() {
        PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager();
        connMgr.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        connMgr.setMaxTotal(MAX_TOTAL_CONNECTIONS);

        this.httpClient = new DefaultHttpClient(connMgr);
        this.httpClient.getParams().setParameter("http.protocol.version",
                HttpVersion.HTTP_1_1);
        this.httpClient.getParams().setParameter("http.socket.timeout",
                DetectLanguage.timeout);
        this.httpClient.getParams().setParameter("http.connection.timeout",
                DetectLanguage.timeout);
        this.httpClient.getParams().setParameter(
                "http.protocol.content-charset", "UTF-8");
    }

    public <T> T execute(String method, Map<String, String> params,
                         Class<T> responseClass) throws APIError {
        Map<String, String> requestParams = new HashMap<String, String>(params);
        requestParams.put("key", DetectLanguage.apiKey);

        URI uri = buildUri(method);
        HttpPost request = new HttpPost(uri);
        request.setEntity(buildPostParams(requestParams));
        addHeaders(request);

        try {
            HttpResponse response = httpClient.execute(request);
            String body = EntityUtils.toString(response.getEntity());
            return processResponse(responseClass, body);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // release of the connection back to the connection manager regardless whether the request execution succeeds or causes an exception
            request.releaseConnection();
        }
    }

    /**
     * When an HttpClient instance is no longer needed and is about to go out of scope it is important to shut down
     * its connection manager to ensure that all connections kept alive by the manager get closed and system resources
     * allocated by those connections are released.
     */
    public void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }

    /**
     * Method is used for testing.
     *
     * @return statistics for current connection pool
     */
    PoolStats getStatistics() {
        PoolStats totalStats = ((PoolingClientConnectionManager) httpClient.getConnectionManager()).getTotalStats();
        return totalStats;
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
    }

    private String buildQueryString(Map<String, String> params) {
        ArrayList<NameValuePair> nvs = new ArrayList<NameValuePair>(
                params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            NameValuePair nv = new BasicNameValuePair(entry.getKey(),
                    entry.getValue());
            nvs.add(nv);
        }
        String queryString = URLEncodedUtils.format(nvs, "UTF-8");
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
            return new UrlEncodedFormEntity(parameters, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
