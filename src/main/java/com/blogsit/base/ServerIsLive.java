package com.blogsit.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerIsLive {
    public static void main(String[] args) {
        System.out.println("连接一下线上机器"+checkServerConnect("172.31.90.46",41122));
    }
    public static boolean checkServerConnect(String ip,int port){
        boolean result =false;
        Socket connect = new Socket();
        try {
            connect.connect(new InetSocketAddress(ip,port),1000);
            result= connect.isConnected();
            return  result;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

}
