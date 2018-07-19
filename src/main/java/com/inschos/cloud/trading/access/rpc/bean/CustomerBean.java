package com.inschos.cloud.trading.access.rpc.bean;

/**
 * author   meiming_mm@163.com
 * date     2018/7/19
 * version  v1.0.0
 */
public class CustomerBean {

    // 业管uuid
    public String managerUuid;
    // 人员类型: 1投保人 2被保人 3受益人
    public String personType;
    // 证件类型：1-身份证，2-护照，3-军官证
    public String cardType;
    // 证件号码
    public String cardCode;

    public String startTime;

    public String endTime;

    public int times;

    public String premium;
}
