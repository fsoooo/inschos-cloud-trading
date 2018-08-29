package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/5/9 on 23:35
 * 描述：
 * 作者：zhangyunhe
 */
public class ProductBean {

    public long id;
    public String name;
    public String displayName;
    public String content;
    public long insuranceCoId;
    public String insuranceCoName;
    public long categoryId;
    public String categoryName;
    public int minPeople;
    public int maxPeople;
    public int sellStatus;
    public int insStatus;
    public String basePrice;
    public String baseBrokerage;
    public int payType;
    public int observationPeriod;
    public int coolingOffPeriod;
    public String latestDate;
    public String earliestDate;
    //private_code
    public String code;
    public String apiUuid;

    public long payCategoryId;
    public String payCategoryName;
    public int payCategoryTimes;
    public String payCategoryTimesUnit;

    public String amount;

    @Override
    public String toString() {
        return "ProductBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", content='" + content + '\'' +
                ", insuranceCoId=" + insuranceCoId +
                ", insuranceCoName='" + insuranceCoName + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", minPeople=" + minPeople +
                ", maxPeople=" + maxPeople +
                ", sellStatus=" + sellStatus +
                ", insStatus=" + insStatus +
                ", basePrice='" + basePrice + '\'' +
                ", baseBrokerage='" + baseBrokerage + '\'' +
                ", payType=" + payType +
                ", observationPeriod=" + observationPeriod +
                ", coolingOffPeriod=" + coolingOffPeriod +
                ", latestDate='" + latestDate + '\'' +
                ", earliestDate='" + earliestDate + '\'' +
                ", code='" + code + '\'' +
                ", payCategoryId=" + payCategoryId +
                ", payCategoryName='" + payCategoryName + '\'' +
                ", payCategoryTimes=" + payCategoryTimes +
                ", payCategoryTimesUnit='" + payCategoryTimesUnit + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
