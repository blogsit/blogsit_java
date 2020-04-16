package com.blogsit.httpclient;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.Future;

public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);
    /**
     * 这种使用的是默认配置,而且是singleton
     */
    private BAsynHttpClient asyncHttpClient = BAsynHttpClient.defBuild();
    /**
     * 不建议使用new 对象,使用spring 注入,new不是singleton
     */
    private BAsynHttpClient asyncHttpClient1 = new BAsynHttpClient(2000, 200, 50, 20);

    /**
     * 原生的response 不需要进行任何对返回的数据进行任何的序列化
     */
    public void reponse() {
        // Future<Response> f = asyncHttpClient.prepareGet("http://www.baidu.com/").execute();
        HashMap<String, String> map = new HashMap<>();
        map.put("message", "test");
        Future<Response> f1 = asyncHttpClient.preparePost("http://www.baidu.com/", map).execute();
        try {
            Response response = f1.get();
            //   Response response1 = f.get();
            logger.info("response value:{}", response.getResponseBody());
            // logger.info("response baidu value:{}", response1.getResponseBody());
        } catch (Exception e) {
            logger.error("get value error:{}", e);
        }
    }

    public void responseString() {
        Future<String> f = asyncHttpClient.prepareGet("http://www.example.com/").execute(new AsyncCompletionHandler<String>() {
            @Override
            public void onThrowable(Throwable t) {
                // Something wrong happened.
            }

            @Override
            public String onCompleted(Response response) throws Exception {
                return response.getResponseBody();
            }
        });
        try {
            String test = f.get();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 同步httpClient请求
     */
    BHttpClient bHttpClient = new BHttpClient(2000, 500, 100, 20, 1);

    public void synchronizeHttp() {
        String retVuale = bHttpClient.httpGet("www.baidu.com");
        if (retVuale == null || retVuale.length() == 0) {
            //如果返回的是空,表示服务有readTimeOut 或者connectTimeOut
            // ,但有一种情况是没有办法进行排除,返回的body没有任何内容.
            //httpStatus 方法返回的是状态.
            logger.error("get data error:{}", retVuale);
        }
    }

    /**
     * 当进程结束时,一定需要close chanel pool,特别注意close不是关闭stream, 因此在运行中不需要调用close 方法,只有整个进程结束时才调用close方法
     */
    public void shutdown() {
        try {
            asyncHttpClient.close();
            asyncHttpClient1.close();
            bHttpClient.closeConnectMonitor();
        } catch (Exception e) {
            logger.error("close the channel error", e);
        }
    }

   public static void main(String[] args) {
        Example example = new Example();
        example.reponse();
        example.shutdown();

    }
}

