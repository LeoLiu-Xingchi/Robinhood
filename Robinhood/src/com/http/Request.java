package com.http;

import com.sun.deploy.net.HttpRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by billupus on 11/11/18.
 */
public class Request {
    private URL baseUrl;
    private RequestType type;
    private Map<String, String> parameters = new HashMap<String, String>();
    private Map<String, String> headers = new HashMap<String, String>();

    public Request(String url, RequestType type) throws MalformedURLException {
        this.baseUrl = new URL(url);
        this.type = type;
    }

    public Request addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public Request addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Request setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public Request setType(RequestType type) {
        this.type = type;
        return this;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public RequestType getType() {
        return type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public enum RequestType {
        GET("GET"),
        POST("POST");

        private String type;

        RequestType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
