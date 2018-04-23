package com.inschos.cloud.trading.extend.car;

/**
 * 创建日期：2018/3/29 on 14:41
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInsuranceResponse {

    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 0;
    public static final String ERROR_SI100100000063 = "SI100100000063";

    public String sendTime;
    public String sign;
    public int state;
    public String msg;
    public String msgCode;

    // 验证签名用
    public boolean verify;

}
