package com.inschos.cloud.trading.assist.kit;

import okhttp3.*;

import java.io.IOException;

/**
 * 创建日期：2018/3/28 on 17:30
 * 描述：
 * 作者：zhangyunhe
 */
public class HttpClientKit {

    private static OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
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

}
