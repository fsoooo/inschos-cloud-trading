package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.InsuranceConciseInfo;
import hprose.client.HproseHttpClient;

/**
 * 创建日期：2018/4/17 on 11:53
 * 描述：
 * 作者：zhangyunhe
 */
public class InsuranceClient {

    private final String remoteUrl = "http://localhost:9600/test.php";

    private HproseHttpClient client = new HproseHttpClient(remoteUrl);

    public InsuranceConciseInfo getInsuranceConciseInfo(String token){
        return new InsuranceConciseInfo();
    }

}
