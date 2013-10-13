package com.detectlanguage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
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
import org.apache.http.util.EntityUtils;

import com.detectlanguage.errors.APIError;
import com.detectlanguage.responses.ErrorData;
import com.detectlanguage.responses.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Client {

	private static final String AGENT = "detectlanguage-java";

	private final HttpClient httpClient;

	public Client() {
		PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager();
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
		URI uri = buildUri(method);

		HttpPost request = new HttpPost(uri);

		addHeaders(request);

		params.put("key", DetectLanguage.apiKey);

		request.setEntity(buildPostParams(params));

		HttpResponse response;

		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String body;

		try {
			body = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return processResponse(responseClass, body);
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
