package com.blogsit.httpclient;

import com.alibaba.fastjson.JSON;
import com.blogsit.httpclient.Entity.HttpReturn;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(BHttpClient.class);

    private final CloseableHttpClient httpClient;
    private int connTimeout = 500;
    private int readTimeout = 2000;
    private int maxTotal = 200;
    private int MaxPerRoute = 50;
    private final String chrasetSet = "UTF-8";
    private final IdleConnectionMonitorThread idleConnectionMonitorThread;
    private final String defaultUserAgent = "Apache-HttpClient/4.5.3 (Java/1.8.0_20)";

    /**
     * 自定义maxTotal=200,maxPerRoute=50　 如果只有一个请求,maxTotal=maxPerRouter, 一般情况最大总数大于每个路由总数.
     */
    private BHttpClient() {
        ConnectionKeepAliveStrategy myStrategy = new MyStrategy();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(maxTotal);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(MaxPerRoute);
        // Increase max connections for localhost:80 to 5;
        RequestConfig config = RequestConfig.custom().setSocketTimeout(readTimeout).setConnectTimeout(connTimeout)
                .setConnectionRequestTimeout(connTimeout).build();
        httpClient = HttpClients.custom().setUserAgent(defaultUserAgent).setDefaultRequestConfig(config).setKeepAliveStrategy(myStrategy)
                .setConnectionManager(cm).setRetryHandler(new RetryTarget(0)).build();
        idleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
        idleConnectionMonitorThread.start();
    }

    /**
     * 如果自定义BHttpClient,建议为全局使用,new ThttpClient比较损耗性能,该类是线程安全的.
     *
     * @param readTimeOut 获取数据的超时时间.
     * @param maxTotal    http 连接池最大数量
     * @param maxPerRoute 每个url对应的最大连接数,有两个test.blogsit.com和test1.blogsit.com,设置为20,
     *                    那么test.blogsit.com最大连接数为20,test1.blogsit.com最大连接数为也为20
     * @param connTimeout 建立连接所需要的时间,
     * @param retryTime   重试次数,如果retryTime<=0表示不重试,read timeout,connect timeout 都会重试! 重试不能保证业务的幂等
     */
    public BHttpClient(int readTimeOut, int connTimeout, int maxTotal, int maxPerRoute, int retryTime) {
        ConnectionKeepAliveStrategy myStrategy = new MyStrategy();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        RequestConfig config = RequestConfig.custom().setSocketTimeout(readTimeOut).setConnectTimeout(connTimeout)
                .setConnectionRequestTimeout(connTimeout).build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(config).setKeepAliveStrategy(myStrategy)
                .setConnectionManager(cm).setRetryHandler(new RetryTarget(retryTime)).build();
        idleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
        idleConnectionMonitorThread.start();
    }

    public BHttpClient(int readTimeOut, int connTimeout, int maxTotal, int maxPerRoute, int retryTime, String userAgent) {
        ConnectionKeepAliveStrategy myStrategy = new MyStrategy();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        RequestConfig config = RequestConfig.custom().setSocketTimeout(readTimeOut).setConnectTimeout(connTimeout)
                .setConnectionRequestTimeout(connTimeout).build();
        httpClient = HttpClients.custom().setUserAgent(userAgent).setDefaultRequestConfig(config).setKeepAliveStrategy(myStrategy)
                .setConnectionManager(cm).setRetryHandler(new RetryTarget(retryTime)).build();
        idleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
        idleConnectionMonitorThread.start();
    }


    private static class singletonHolder {
        private final static BHttpClient lastestHttpClient = new BHttpClient();
    }

    public static BHttpClient clientBuild() {
        return singletonHolder.lastestHttpClient;
    }

    /**
     * httpGet url中的参数建议进行UTF-8编码
     */
    public String httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        String retStr = "";
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            retStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
            EntityUtils.consume(httpEntity);
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.error("get data from url={} ,return value={}", url, retStr);
                return "";
            }
        } catch (Exception e) {
            logger.error("read data from url={} error", url, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("close response error", e);
            }


        }
        return retStr;
    }

    public HttpReturn httpGet(HttpGet httpGet) {
        if (httpGet == null || httpGet.getURI() == null) {
            throw new IllegalArgumentException("httpGet can not be null");
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String retStr = EntityUtils.toString(httpEntity, Charset.forName("UTF-8"));
            return new HttpReturn(response.getStatusLine().getStatusCode(), retStr);
        } catch (Exception e) {
            logger.error("read data from url={} error", httpGet.getURI(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("close response error", e);
            }
        }
        return null;
    }

    public HttpReturn httpGetStauts(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String retStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
            EntityUtils.consume(httpEntity);
            return new HttpReturn(response.getStatusLine().getStatusCode(), retStr);
        } catch (Exception e) {
            logger.error("read data from url={} error", url, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("close response error", e);
            }
        }
        return null;
    }

    /**
     * 返回执行状态.
     */
    public int httpStatus(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        int status = -1;
        try {
            response = httpClient.execute(httpGet);
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            logger.error("get url={} error", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("close response error", e);
            }
        }
        return status;
    }

    /**
     * post请求，BasicNameValuePair key为parameter name ,value 为 parameter value
     */
    public String httpPost(String url, List<BasicNameValuePair> parameters) {
        if (parameters == null || parameters.size() == 0) {
            return null;
        }
        String retStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(parameters, chrasetSet));
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            retStr = IOUtils.toString(httpEntity.getContent(), chrasetSet);
            EntityUtils.consume(httpEntity);
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.error("url={}, parameter={},retSt value is:{}", url, JSON.toJSONString(parameters), retStr);
                return "";
            }
        } catch (Exception e) {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e1) {
                logger.error("close  response error", e1);
            }
            logger.error("post url={},parameters={} error:", url, JSON.toJSONString(parameters), e);
        }
        return retStr;
    }

    public HttpReturn httpPost(HttpPost httpPost) {
        if (httpPost == null || httpPost.getURI() == null) {
            throw new IllegalArgumentException("httpPost can not be null");
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String retStr = EntityUtils.toString(httpEntity, Charset.forName("UTF-8"));
            return new HttpReturn(response.getStatusLine().getStatusCode(), retStr);
        } catch (Exception e) {
            logger.error("http post error,url={}", httpPost.getURI(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("close response error", e);
            }
        }
        return null;
    }

    /**
     * post请求,返回HttpReturn,包括返回的结果和状态码 如果出现connectTimeOut或者readTimeOut,返回结果为空.
     */
    public HttpReturn postStatus(String url, List<BasicNameValuePair> parameters) {
        if (parameters == null || parameters.size() == 0) {
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(parameters, chrasetSet));
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String retStr = IOUtils.toString(httpEntity.getContent(), chrasetSet);
            EntityUtils.consume(httpEntity);
            return new HttpReturn(response.getStatusLine().getStatusCode(), retStr);
        } catch (Exception e) {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e1) {
                logger.error("close  response error", e1);
            }
            logger.error("post url={},parameters={} error:", url, JSON.toJSONString(parameters), e);
        }
        return null;
    }


    public String httpPostJson(String url, String jsonString) {
        if (jsonString == null) {
            return null;
        }
        String retStr = null;
        CloseableHttpResponse response = null;
        try {
            StringEntity requestEntity = new StringEntity(
                    jsonString,
                    ContentType.APPLICATION_JSON);
            HttpPost postMethod = new HttpPost(url);
            postMethod.setEntity(requestEntity);
            response = httpClient.execute(postMethod);
            HttpEntity httpEntity = response.getEntity();
            retStr = IOUtils.toString(httpEntity.getContent(), chrasetSet);
            EntityUtils.consume(httpEntity);
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.error("url={}, parameter={},retSt value is:{}", url, jsonString, retStr);
                return "";
            }
        } catch (Exception e) {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e1) {
                logger.error("close  response error", e1);
            }
            logger.error("post url={},parameters={} error:", url, jsonString, e);
        }
        return retStr;
    }

    public HttpReturn postHeadStatus(String url, String bodyString, ContentType contentType) {
        if (bodyString == null) {
            return null;
        }
        CloseableHttpResponse response = null;
        try {
            StringEntity requestEntity = new StringEntity(
                    bodyString,
                    contentType);
            HttpPost postMethod = new HttpPost(url);
            postMethod.setEntity(requestEntity);
            response = httpClient.execute(postMethod);
            HttpEntity httpEntity = response.getEntity();
            String retStr = IOUtils.toString(httpEntity.getContent(), chrasetSet);
            EntityUtils.consume(httpEntity);
            HttpReturn httpReturn = new HttpReturn(response.getStatusLine().getStatusCode(), retStr);
            return httpReturn;

        } catch (Exception e) {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e1) {
                logger.error("close  response error", e1);
            }
            logger.error("post url={},parameters={} error:", url, bodyString, e);
        }
        return null;
    }

    /**
     * post请求,参数在body内.参数的格式为json
     */
    public HttpReturn postJsonStatus(String url, String jsonString) {
        return postHeadStatus(url, jsonString, ContentType.APPLICATION_JSON);
    }

    /**
     * post 封装,将 hashMap 转换成list BasicNameValuePairs
     */
    public String httpPostMap(String url, Map<String, String> hashMap) {
        if (hashMap == null) {
            logger.error("post data parameter null");
            return "";
        }

        List<BasicNameValuePair> list = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return httpPost(url, list);
    }

    /**
     * 如果使用spring 在spring destroy 方法中调用该方法,手动关闭closeConnect
     */
    public void closeConnectMonitor() {
        if (idleConnectionMonitorThread != null) {
            idleConnectionMonitorThread.shutdown();
        }
    }
}

class MyStrategy implements ConnectionKeepAliveStrategy {
    private final static Logger logger = LoggerFactory.getLogger(MyStrategy.class);

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        // Honor 'keep-alive' header
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000;
                } catch (NumberFormatException ignore) {
                    logger.error("numberFormatException:", ignore);
                }
            }
        }
        // HttpHost target = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
        return 30 * 1000l;
    }
}

/**
 * One of the major shortcomings of the classic blocking I/O model is that the network socket can
 * react to I/O events only when blocked in an I/O operation. 对底层的网络连接是未知的
 */
class IdleConnectionMonitorThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            logger.warn("IdleConnection thread have been close", ex);
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}

/**
 * 重试策略, readTimeOut connectTimeOut都会重试,业务幂等不要使用该策略
 */
class RetryTarget implements HttpRequestRetryHandler {

    private final int retryCount;

    public RetryTarget(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= retryCount + 1) {
            return false;
        }
        if (exception instanceof UnknownHostException) {
            return false;
        }

        if (exception instanceof SSLException) {
            // SSL handshake exception
            return false;
        }

        if (exception instanceof NoHttpResponseException) {
            return true;
        }

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        if (idempotent) {
            // Retry if the request is considered idempotent 方法幂等
            return true;
        }
        return false;
    }
}


