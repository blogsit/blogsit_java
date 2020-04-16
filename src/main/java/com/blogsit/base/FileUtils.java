package com.blogsit.base;

import com.alibaba.fastjson.JSON;
import com.blogsit.httpclient.BHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


/**
 *
 */
public class FileUtils {
    //单次增加的值
    public static final int ADD_COUNT = 10;
    public static final String FILE_PATH = "/Users/chenhua/Documents/Dropbox/importfile/all_phone_81000.txt";
    public static final String OUT_PUTT_FILE_PATH = "/Users/chenhua/Documents/Dropbox/importfile/all_phone_out_81000.txt";

    public static void main(String[] args) throws Exception {
        /**
         * 同步httpClient请求
         */
        BHttpClient bHttpClient = new BHttpClient(2000, 500, 100, 20, 1);

        /**
         * 创建输出的文件
         */
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PUTT_FILE_PATH));

        /**
         * 创建写入的文件
         */
        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_PATH));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String resultData = bHttpClient.httpGet(line);
            if (StringUtils.isNotBlank(resultData)) {
                APIResponse apiResponse = JSON.parseObject(resultData, APIResponse.class);
                if (apiResponse.isRet() && StringUtils.isBlank(apiResponse.getErrmsg())) {
                    System.out.println("发送成功了----------" + apiResponse.getData().toString());
                    bufferedWriter.write(apiResponse.getData().toString());
                    bufferedWriter.newLine();
                } else {
                    bufferedWriter.write("请求失败");
                    bufferedWriter.newLine();
                }
            } else {
                bufferedWriter.write("解析失败");
                bufferedWriter.newLine();
            }
            Thread.sleep(10);
        }
        //关闭写入文件流
        bufferedWriter.flush();
        bufferedWriter.close();

        //关闭读取文件流
        bufferedReader.close();


    }
}
