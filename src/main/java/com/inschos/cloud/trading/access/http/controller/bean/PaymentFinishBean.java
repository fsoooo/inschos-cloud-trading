package com.inschos.cloud.trading.access.http.controller.bean;

/**
 * 创建日期：2018/3/28 on 16:16
 * 描述：
 * 作者：zhangyunhe
 */
public class PaymentFinishBean extends BaseRequest {

    // pay_call_back
    public String notice_type;
    public PaymentFinishData data;

    public static class PaymentFinishData {
        /**
         * 支付状态
         */
        public boolean status;

        /**
         * 佣金比例
         */
        public String ratio_for_agency;

        /**
         * 佣金值
         */
        public String brokerage_for_agency;

        /**
         * 联合订单号
         */
        public String union_order_code;

        /**
         * 缴费分期形式 0趸交
         */
        public String by_stages_way;

        /**
         * 错误信息
         */
        public String error_message;
    }

}
