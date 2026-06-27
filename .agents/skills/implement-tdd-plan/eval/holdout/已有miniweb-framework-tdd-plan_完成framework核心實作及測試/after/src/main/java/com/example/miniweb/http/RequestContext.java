package com.example.miniweb.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestContext {
    private final String method;
    private final String path;
    private final Map<String, String> pathParams;
    private final ResponseWriter responseWriter;

    public RequestContext(String method, String path, ResponseWriter responseWriter) {
        this(method, path, new LinkedHashMap<>(), responseWriter);
    }

    private RequestContext(String method, String path, Map<String, String> pathParams, ResponseWriter responseWriter) {
        this.method = method;
        this.path = path;
        this.pathParams = pathParams;
        this.responseWriter = responseWriter;
    }

    public RequestContext withPathParams(Map<String, String> pathParams) {
        return new RequestContext(method, path, new LinkedHashMap<>(pathParams), responseWriter);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    public ResponseWriter getResponseWriter() {
        return responseWriter;
    }
}
