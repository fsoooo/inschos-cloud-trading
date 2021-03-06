package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.model.BrokerageStatistic;
import com.inschos.cloud.trading.model.PremiumStatistic;
import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;

import java.util.List;

/**
 * Created by IceAnt on 2018/5/16.
 */
public class StatisticBean {

    public static class InsureStsListForAgentSelfRequest extends BaseRequest{

        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String startTime;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String endTime;

        @CheckParams
        public String timeRangeType;

    }

    public static class InsureStsListForAgentSelfResponse extends BaseResponse{
        public List<InsureStsItem> data;
    }

    public static class InsureStsTotalForAgentSelfResponse extends BaseResponse{
        public InsureTotalData data;
    }

    public static class InsureTotalData{
        public InsureTotalStatistic warranty ;
        public InsureTotalStatistic premium ;
        public InsureTotalStatistic brokerage;
    }

    public static class InsureTotalStatistic {
        public String dayAmount;
        public String monthAmount;
        public String totalAmount;
    }


    public static class InsureStsItem {
        public String timeText;
        public String time;
        public String insurancePolicyCount;

        public String premium;
        public String premiumText;
        public String averagePremium;
        public String averagePremiumText;
        public String premiumPercentage;
        public String premiumPercentageText;

        public String brokerage;
        public String brokerageText;
        //        public String brokeragePercentage;
//        public String brokeragePercentageText;
        public String averageBrokeragePercentage;
        public String averageBrokeragePercentageText;

        public InsureStsItem() {

        }

        public InsureStsItem(String timeText, String type) {
            this.timeText = timeText;
            switch (type){
                case "2":
                    this.time = String.valueOf(TimeKit.parse(timeText,"yyyy-MM-dd"));
                    break;
                case "3":
                    this.time = String.valueOf(TimeKit.parse(timeText+"-01","yyyy-MM-dd"));
                    break;
            }

        }

        public void setPremiumStatisticModel(PremiumStatistic premiumStatistic) {
            if (premiumStatistic == null) {
                return;
            }

            if (!StringKit.isEmpty(premiumStatistic.premium) && StringKit.isNumeric(premiumStatistic.premium)) {
                this.premium = premiumStatistic.premium;
                this.premiumText = "¥" + this.premium;
            } else {
                this.premium = "0.00";
                this.premiumText = "¥0.00";
            }

            if (!StringKit.isEmpty(premiumStatistic.insurance_policy_count) && StringKit.isInteger(premiumStatistic.insurance_policy_count)) {
                this.insurancePolicyCount = premiumStatistic.insurance_policy_count;
            } else {
                this.insurancePolicyCount = "0";
            }

        }

        public void setBrokerageStatisticModel(BrokerageStatistic brokerageStatistic) {
            if (brokerageStatistic == null) {
                return;
            }

            if (!StringKit.isEmpty(brokerageStatistic.brokerage) && StringKit.isNumeric(brokerageStatistic.brokerage)) {
                this.brokerage = brokerageStatistic.brokerage;
                this.brokerageText = "¥" + this.brokerage;
            } else {
                this.brokerage = "0.00";
                this.brokerageText = "¥0.00";
            }
        }
    }
}
