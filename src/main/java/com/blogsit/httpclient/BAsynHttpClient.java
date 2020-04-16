package com.blogsit.httpclient;

import org.apache.http.protocol.HTTP;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 可以参见Example类 可以参考官方使用文档https://github.com/AsyncHttpClient/async-http-client
 */
public class BAsynHttpClient implements AsyncHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(BAsynHttpClient.class);
    private final AsyncHttpClient asyHttpClient;
    private int readTime = 2000;
    private int connectTime = 100;
    private int connectPoolSize = 200;
    private int perRoutMax = 50;
    private int idleTimeout = 10000;
    private final String contentValue = "application/json";
    /**
     * The handling of stale connections was changed in version 4.4. Previously, the code would
     * check every connection by default before re-using it. The code now only checks the connection
     * if the elapsed time since the last use of the connection exceeds the timeout that has been
     * set. The default timeout is set to 2000ms 检查连接是否可用,如果在上次连接可用时间+ttl内,不会检测连接是否可用
     */
    private int ttl = 2000;

    private BAsynHttpClient() {
        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxConnections(connectPoolSize)
                .setMaxConnectionsPerHost(perRoutMax).setPooledConnectionIdleTimeout(idleTimeout).setReadTimeout(readTime)
                .setConnectionTtl(ttl).setConnectTimeout(connectTime).build();
        asyHttpClient = new DefaultAsyncHttpClient(config);

    }

    /**
     * 不建议使用new 对象,使用spring 注入,new不是singleton
     *
     * @param readTime        请求响应时间
     * @param connectTime     连接时间,因都是内网调用,建议连接时间超时在1s内.
     * @param connectPoolSize 连接池大小
     * @param perRoutMax      　每个路由链接数量
     */
    public BAsynHttpClient(int readTime, int connectTime, int connectPoolSize, int perRoutMax) {
        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxConnections(connectPoolSize)
                .setMaxConnectionsPerHost(perRoutMax).setPooledConnectionIdleTimeout(idleTimeout).setReadTimeout(readTime)
                .setConnectionTtl(ttl).setConnectTimeout(connectTime).build();
        asyHttpClient = new DefaultAsyncHttpClient(config);
    }


    /**
     * 关闭channel pool,当整个程序运行完时,在程序运行中,不需要调用该方法,该方法不是关闭stream. 如果使用的defBuild产生的asynHtpClient,直接调用close方法
     * jar包调用一定需要调用close方法，否则进程不会结束. war包调用因公司的tomcat stop脚本会kill -9,可以不需要close.建议进程结束时调用该方法．
     */
    public void shutdown() {
        try {
            asyHttpClient.close();
        } catch (Exception e) {
            logger.error("close the channel error");
        }
    }


    @Override
    public boolean isClosed() {
        return asyHttpClient.isClosed();
    }


    /**
     * get请求,返回结果需要执行executor()方法. prepareGet("http://localhost:8080/status/group?message={\"test\":\"猛\"}").execute()
     * 如果需要设置额外的参数，需要设置cookie,asyHttpClient.prepareGet(url).setCookies()该方法,
     * BoundRequestBuilder是一个工厂构造器,通过set可以设置对应的参数.如果不设置使用默认.
     */
    @Override
    public BoundRequestBuilder prepareGet(String url) {
        return asyHttpClient.prepareGet(url);
    }

    /**
     * @param url       post表单请求
     * @param paramters post参数,key value 形式
     */
    public BoundRequestBuilder preparePost(String url, Map<String, String> paramters) {
        List<Param> list = new ArrayList<>(paramters.size());
        Iterator<Map.Entry<String, String>> iterator = paramters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            list.add(new Param(entry.getKey(), entry.getValue()));
        }
        return asyHttpClient.preparePost(url).setFormParams(list);
    }

    public BoundRequestBuilder postJson(String url, String json) {
        return asyHttpClient.preparePost(url).setBody(json).setHeader(HTTP.CONTENT_TYPE, contentValue);
    }

    /**
     * url post
     */
    @Override
    public BoundRequestBuilder preparePost(String url) {
        return asyHttpClient.preparePost(url);
    }

    @Override
    public AsyncHttpClient setSignatureCalculator(SignatureCalculator signatureCalculator) {
        return asyHttpClient.setSignatureCalculator(signatureCalculator);
    }

    @Override
    public BoundRequestBuilder prepare(String method, String url) {
        return asyHttpClient.prepare(method, url);
    }

    @Override
    public BoundRequestBuilder prepareConnect(String url) {
        return asyHttpClient.prepareConnect(url);
    }

    @Override
    public BoundRequestBuilder prepareOptions(String url) {
        return asyHttpClient.prepareOptions(url);
    }

    @Override
    public BoundRequestBuilder prepareHead(String url) {
        return asyHttpClient.prepareHead(url);
    }


    @Override
    public BoundRequestBuilder preparePut(String url) {
        return asyHttpClient.preparePut(url);
    }

    @Override
    public BoundRequestBuilder prepareDelete(String url) {
        return asyHttpClient.prepareDelete(url);
    }

    @Override
    public BoundRequestBuilder preparePatch(String url) {
        return asyHttpClient.preparePatch(url);
    }

    @Override
    public BoundRequestBuilder prepareTrace(String url) {
        return asyHttpClient.prepareTrace(url);
    }

    @Override
    public BoundRequestBuilder prepareRequest(Request request) {
        return asyHttpClient.prepareRequest(request);
    }

    @Override
    public BoundRequestBuilder prepareRequest(RequestBuilder requestBuilder) {
        return asyHttpClient.prepareRequest(requestBuilder);
    }

    @Override
    public <T> ListenableFuture<T> executeRequest(Request request, AsyncHandler<T> handler) {
        return asyHttpClient.executeRequest(request, handler);
    }

    @Override
    public <T> ListenableFuture<T> executeRequest(RequestBuilder requestBuilder, AsyncHandler<T> handler) {
        return asyHttpClient.executeRequest(requestBuilder, handler);
    }

    @Override
    public ListenableFuture<Response> executeRequest(Request request) {
        return asyHttpClient.executeRequest(request);
    }

    @Override
    public ListenableFuture<Response> executeRequest(RequestBuilder requestBuilder) {
        return asyHttpClient.executeRequest(requestBuilder);
    }

    @Override
    public ClientStats getClientStats() {
        return asyHttpClient.getClientStats();
    }

    @Override
    public void flushChannelPoolPartitions(Predicate<Object> predicate) {
        asyHttpClient.flushChannelPoolPartitions(predicate);
    }

    @Override
    public AsyncHttpClientConfig getConfig() {
        return asyHttpClient.getConfig();
    }

    @Override
    public void close() throws IOException {
        asyHttpClient.close();
    }

    private static class singletonHolder {
        private final static BAsynHttpClient asynHtpClient = new BAsynHttpClient();
    }

    public AsyncHttpClient build() {
        return asyHttpClient;
    }

    /**
     * 该方法返回的是singleton
     */
    public static BAsynHttpClient defBuild() {
        return singletonHolder.asynHtpClient;
    }
}
