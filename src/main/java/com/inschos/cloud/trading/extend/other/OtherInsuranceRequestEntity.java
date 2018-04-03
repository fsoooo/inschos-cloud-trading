package com.inschos.cloud.trading.extend.other;

/**
 * 创建日期：2018/4/3 on 16:03
 * 描述：
 * 作者：zhangyunhe
 */
public class OtherInsuranceRequestEntity<T> {

    public String account_id = "123456789";
    public String sign;
    public String biz_content;
    public String timestamp = String.valueOf(System.currentTimeMillis());

    public T data;

}
