package com.donaldy.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpRequestBuilder {
    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

    private String type;
    private String url;
    private Map<String, String> headers;
    private ContentType contentType;
    private String body;
    private HttpRequestBase httpRequestBase;

    public HttpRequestBuilder(String type) {
        this.type = type;
    }

    public static HttpRequestBuilder get() {
        return new HttpRequestBuilder("get");
    }

    public static HttpRequestBuilder post() {
        return new HttpRequestBuilder("post").contentType(ContentType.APPLICATION_FORM_URLENCODED);
    }

    public HttpRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public HttpRequestBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpRequestBuilder body(String body) {
        this.body = body;
        return this;
    }

    public HttpRequestBuilder body(Map<String, String> params) {
        List<String> nameValuePairs = new LinkedList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()).toString());
        }
        this.body = Joiner.on("&").join(nameValuePairs);
        return this;
    }

    public HttpRequestBuilder build() {
        if (type.equals("get")) {
            httpRequestBase = new HttpGet(url);
        } else {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(body, "UTF-8"));
            httpRequestBase = httpPost;
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequestBase.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (contentType != null) {
            httpRequestBase.addHeader(HTTP.CONTENT_TYPE, contentType.getMimeType());
        }
        return this;
    }

    public String invoke() {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
        try {
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpRequestBase);
            return parseHttpResponse(closeableHttpResponse);
        } catch (IOException e) {
            return null;
        }
    }

    public <T> T invoke(TypeReference<T> tClass) {
        String invoke = invoke();
        return JSONObject.parseObject(invoke, tClass);
    }

    public <T> T invoke(Class<T> tClass) {
        String invoke = invoke();
        return JSONObject.parseObject(invoke, tClass);
    }

    /**
     *  deal with - get部分请求乱码问题
     */
    public <T> T invoke(TypeReference<T> tClass , String charsetName) {
        String invoke = null;
        try {
            invoke = new String(invoke().getBytes(charsetName),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.info("解码异常，请重新设置解码规则", e);
        }
        return JSONObject.parseObject(invoke, tClass);
    }

    public static String parseHttpResponse(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                return EntityUtils.toString(entity);
            } catch (IOException e) {
                log.error("", e);
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return null;
    }

    public HttpRequestBuilder header(String name, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        return this;
    }
}