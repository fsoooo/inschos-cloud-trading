package com.inschos.cloud.trading.extend.other;

import java.util.List;

/**
 * 创建日期：2018/4/3 on 16:09
 * 描述：
 * 作者：zhangyunhe
 */
public class ExtendOtherInsurancePolicy {

    public static class GetQuoteRequest extends OtherInsuranceRequest {
        public List<ProtectItem> new_val;
        public List<ProtectItem> old_val;
        public String private_p_code;
        public List<ProtectItem> old_option;
        public List<ProtectItem> old_protect_item;
    }

    public static class GetQuoteResponse extends OtherInsuranceResponse {
        public GetQuote data;
    }

    public static class GetQuote {
        public String price;

    }

    public static class ProtectItem {
        public String protectItemId;
        public String description;
        public String value;
        public String sort;
        public String name;
        public boolean display;
        public String defaultValue;
        public String type;

        // 响应字段
        public String ty_key;
        // 请求字段
        public String key;

        public List<ProtectItemSelection> values;
    }

    public static class ProtectItemSelection {
        public String type;
        public String max;
        public String min;
        public String step;
        public String unit;
    }

    public static class GetBuyInsuranceRequest extends OtherInsuranceRequest {

    }

    public static class GetBuyInsuranceResponse extends OtherInsuranceResponse {
        public GetBuyInsurance data;
    }

    public static class GetBuyInsurance {

    }

    public static class GetPayWayInfoRequest extends OtherInsuranceRequest {

    }

    public static class GetPayWayInfoResponse extends OtherInsuranceResponse {
        public GetPayWayInfo data;
    }

    public static class GetPayWayInfo {

    }

    public static class GetOrderRequest extends OtherInsuranceRequest {

    }

    public static class GetOrderResponse extends OtherInsuranceResponse {
        public GetOrder data;
    }

    public static class GetOrder {

    }

}
