package com.inschos.cloud.trading.access.rpc.client;

import hprose.client.HproseHttpClient;

/**
 * Created by IceAnt on 2018/4/16.
 */
public class BaseClientService {


    public <T> T getService(String url,Class<T> tClass){
        return new HproseHttpClient(url).useService(tClass);
    }
}
