package com.blogsit.httpclient.Entity;

public class HttpReturn {

    public HttpReturn(int statusCode,String body){
        this.statusCode=statusCode;
        this.body=body;
    }
    private int statusCode;
    private String body;

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
