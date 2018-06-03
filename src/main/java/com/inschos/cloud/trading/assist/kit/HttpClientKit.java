package com.inschos.cloud.trading.assist.kit;

import okhttp3.*;
import org.springframework.http.RequestEntity;
import sun.net.www.http.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * 创建日期：2018/3/28 on 17:30
 * 描述：
 * 作者：zhangyunhe
 */
public class HttpClientKit {

    private static OkHttpClient client = new OkHttpClient();
    private static OkHttpClient fileClient = new OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build();

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        return post(url, json, client);
    }

    public static String uploadFile(String url, String json) throws IOException {
        return post(url, json, fileClient);
    }

    private static String post(String url, String json, OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String result = "";
        if (response.body() != null) {
            result = response.body().string();
        }
        return result;
    }

    public static InputStream downloadFile(String url) {
        InputStream is = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = fileClient.newCall(request).execute();

            ResponseBody body = response.body();
            if (body != null) {
                is = body.byteStream();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            is = null;
        }

        return is;
    }

}
