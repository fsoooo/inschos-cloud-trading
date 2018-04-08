package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.extend.other.ExtendOtherInsurancePolicy;
import com.inschos.cloud.trading.extend.other.OtherInsuranceCommon;
import com.inschos.cloud.trading.extend.other.OtherInsuranceHttpRequest;
import com.inschos.cloud.trading.extend.other.OtherInsuranceResponse;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/4/3 on 17:38
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class OtherInsuranceAction {

    /**
     * 算费接口
     */
    private static final String get_quote = OtherInsuranceCommon.getServerHost() + "/ins_curl/quote";

    public String getQuote(ActionBean actionBean) {

        ExtendOtherInsurancePolicy.GetQuoteRequest getQuoteRequest = new ExtendOtherInsurancePolicy.GetQuoteRequest();

        ExtendOtherInsurancePolicy.GetQuoteResponse result = new OtherInsuranceHttpRequest<>(get_quote, getQuoteRequest, ExtendOtherInsurancePolicy.GetQuoteResponse.class).post();

        String str;
        if (result.code == OtherInsuranceResponse.RESULT_OK) {

        } else {

        }

        return "";
    }

    /**
     * 投保接口
     */
    private static final String get_buy_insurance = OtherInsuranceCommon.getServerHost() + "/ins_curl/buy_ins";

    public String getBuyInsurance(ActionBean actionBean) {

        ExtendOtherInsurancePolicy.GetBuyInsuranceRequest getBuyInsuranceRequest = new ExtendOtherInsurancePolicy.GetBuyInsuranceRequest();

        ExtendOtherInsurancePolicy.GetBuyInsuranceResponse result = new OtherInsuranceHttpRequest<>(get_buy_insurance, getBuyInsuranceRequest, ExtendOtherInsurancePolicy.GetBuyInsuranceResponse.class).post();


        if (result.code == OtherInsuranceResponse.RESULT_OK) {

        } else {

        }

        return "";
    }

    /**
     * 支付信息
     */
    private static final String get_pay_way_info = OtherInsuranceCommon.getServerHost() + "/ins_curl/get_pay_way_info";

    public String getPayWayInfo(ActionBean actionBean) {

        ExtendOtherInsurancePolicy.GetPayWayInfoRequest getPayWayInfoRequest = new ExtendOtherInsurancePolicy.GetPayWayInfoRequest();

        ExtendOtherInsurancePolicy.GetPayWayInfoResponse result = new OtherInsuranceHttpRequest<>(get_pay_way_info, getPayWayInfoRequest, ExtendOtherInsurancePolicy.GetPayWayInfoResponse.class).post();

        if (result.code == OtherInsuranceResponse.RESULT_OK) {

        } else {

        }

        return "";
    }

    /**
     * 出单接口
     */
    private static final String get_order = OtherInsuranceCommon.getServerHost() + "/ins_curl/issue";

    public String getOrder(ActionBean actionBean) {

        ExtendOtherInsurancePolicy.GetOrderRequest getOrderRequest = new ExtendOtherInsurancePolicy.GetOrderRequest();

        ExtendOtherInsurancePolicy.GetOrderResponse result = new OtherInsuranceHttpRequest<>(get_order, getOrderRequest, ExtendOtherInsurancePolicy.GetOrderResponse.class).post();


        if (result.code == OtherInsuranceResponse.RESULT_OK) {

        } else {

        }

        return "";
    }

}
