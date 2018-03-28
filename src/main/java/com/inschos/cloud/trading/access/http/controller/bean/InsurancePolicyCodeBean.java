package com.inschos.cloud.trading.access.http.controller.bean;

/**
 * 创建日期：2018/3/28 on 16:52
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicyCodeBean {

    public static class InsurancePolicyCodeBeanRequest {

        /**
         * 被保人订单号
         */
        private String order_code;

        /**
         * 联合订单号
         */
        private String union_order_code;

        /**
         * 产品唯一码
         */
        private String private_p_code;

    }

    public static class InsurancePolicyCodeBeanResponse {

        /**
         * 0：未生效 1：已生效 2：退保中 3：已退保
         */
        public int status;

        /**
         * 保单号
         */
        public String policy_order_code;

        /**
         * 产品唯一码
         */
        public String private_p_code;

        /**
         * 被保人订单号
         */
        public String order_code;

        /**
         * 起保时间
         */
        public String start_time;

        /**
         * 终保时间
         */
        public String end_time;

    }

}
