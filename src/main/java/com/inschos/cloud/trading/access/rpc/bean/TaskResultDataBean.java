package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/5/17 on 11:39
 * 描述：
 * 作者：zhangyunhe
 */
public class TaskResultDataBean {

    public String dataType;
    public String dataAmount;
    public String userId;
    public String userType;
    public String time;
    public String sign;

    @Override
    public String toString() {
        return "TaskResultDataBean{" +
                "dataType='" + dataType + '\'' +
                ", dataAmount='" + dataAmount + '\'' +
                ", userId='" + userId + '\'' +
                ", userType='" + userType + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
