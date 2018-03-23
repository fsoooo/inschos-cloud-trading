package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.assist.kit.CardCodeKit;
import com.inschos.cloud.trading.assist.kit.StringKit;

import javax.smartcardio.Card;
import java.util.List;

/**
 * 创建日期：2018/3/22 on 14:03
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicy {

    // 获取保单详情
    public static class InsurancePolicyDetailRequest extends BaseRequest {
        public String privateCode;
    }

    public static class InsurancePolicyDetailResponse extends BaseResponse {
        public InsurancePolicyBaseBean data;
    }

    // 获取保单列表(通过用户id与状态)
    public static class InsurancePolicyListByUserIdAndStatusRequest extends BaseRequest {
        public String userId;
        public String status;
    }

    public static class InsurancePolicyListByUserIdAndStatusResponse extends BaseResponse {
        public List<InsurancePolicyBaseBean> data;
    }

    // 获取保单列表(通过用户ID，保单类型，时间，状态，渠道，来源，关键字)
    public static class InsurancePolicyListByOtherInfoRequest extends BaseRequest {
        public String userId;
        // 保单类型
        public String type;
        // 时间
        public String startTime;
        public String endTime;
        public String status;
        // 来源
        public String warrantyFrom;
        // 渠道id
        public String ditchId;
        // 关键字
        public String key;
    }

    public static class InsurancePolicyListByOtherInfoResponse extends BaseResponse {
        public List<InsurancePolicyBaseBean> data;
    }

    public static class InsurancePolicyInsureRequest extends BaseRequest {

        // '客户ID'
        public String userId;

        // '产品ID'
        public String productId;

        // '起保时间'
        public String startTime;

        // '结束时间'
        public String endTime;

        // '购买份数'
        public String count;

        // 投保人
        public PersonInfo policyholder;

        // 被保险人
        public PersonInfo insured;

        // 受益人
        public PersonInfo beneficiary;

    }

    public static class InsurancePolicyInsureResponse extends BaseResponse {

    }

    public static class InsurancePolicySurrenderRequest extends BaseRequest {

        // '客户ID'
        public String userId;

        // '产品ID'
        public String productId;

        public String privateCode;

        public String surrenderReason;
    }

    public static class InsurancePolicySurrenderResponse extends BaseResponse {

    }



    // 待核保
    public static final int ORDER_STATUS_WAITING_UNDERWRITING = 1;
    // 核保失败
    public static final int ORDER_STATUS_UNDERWRITING_FAIL = 2;
    // 未支付-核保成功
    public static final int ORDER_STATUS_UNDERWRITING_UNPAID = 3;
    // 支付中
    public static final int ORDER_STATUS_PAYING = 4;
    // 支付失败
    public static final int ORDER_STATUS_PAYMENT_FAIL = 5;
    // 支付成功
    public static final int ORDER_STATUS_PAYMENT_SUCCESS = 6;
    // 保障中
    public static final int ORDER_STATUS_PROTECTING = 7;
    // 待生效
    public static final int ORDER_STATUS_WAITING_EFFECTIVE = 8;
    // 待续保
    public static final int ORDER_STATUS_WAITING_RENEWAL = 9;
    // 已失效
    public static final int ORDER_STATUS_FAILURE = 10;
    // 已退保
    public static final int ORDER_STATUS_SURRENDER = 11;

    public static class InsurancePolicyBaseBean {
        // id,分页用
        public long id;

        // '保单号'
        public String warrantyCode;

        // '内部保单唯一标识'
        public String privateCode;

        // '保单状态（ 1待核保，2核保失败，3未支付-核保成功，4支付中,5支付失败,6支付成功，7保障中,8待生效,9待续保，10已失效，11已退保）'
        public int status;
        public String statusText;

        // '客户类型 1个人 2企业'
        public int userType;

        // '客户ID'
        public String userId;

        // '产品ID'
        public String productId;

        // '起保时间'
        public String startTime;

        // '结束时间'
        public String endTime;

        public String timeText;

        // '创建时间'
        public String createdTime;

        // '更新时间'
        public String updatedTime;

        // '保单价格'
        public String premium;

        // '购买份数'
        public String count;

        public String createPrivateCode() {
            return "";
        }

        public boolean setStatus(int status) {
            if (status > 11 || status < 1) {
                return false;
            }

            this.status = status;
            return true;
        }

        public void setStatusText(int status) {
            switch (status) {
                case ORDER_STATUS_WAITING_UNDERWRITING:
                    statusText = "待核保";
                    break;
                case ORDER_STATUS_UNDERWRITING_FAIL:
                    statusText = "核保失败";
                    break;
                case ORDER_STATUS_UNDERWRITING_UNPAID:
                    statusText = "未支付-核保成功";
                    break;
                case ORDER_STATUS_PAYING:
                    statusText = "支付中";
                    break;
                case ORDER_STATUS_PAYMENT_FAIL:
                    statusText = "支付失败";
                    break;
                case ORDER_STATUS_PAYMENT_SUCCESS:
                    statusText = "支付成功";
                    break;
                case ORDER_STATUS_PROTECTING:
                    statusText = "保障中";
                    break;
                case ORDER_STATUS_WAITING_EFFECTIVE:
                    statusText = "待生效";
                    break;
                case ORDER_STATUS_WAITING_RENEWAL:
                    statusText = "待续保";
                    break;
                case ORDER_STATUS_FAILURE:
                    statusText = "已失效";
                    break;
                case ORDER_STATUS_SURRENDER:
                    statusText = "已退保";
                    break;
            }
        }
    }

    public static class InsurancePolicyDetailForPersonBean extends InsurancePolicyBaseBean {

        // 投保人
        public List<PersonInfo> policyholders;

        // 被保险人
        public List<PersonInfo> insureds;

    }

    public static class InsurancePolicyDetailForCompanyBean extends InsurancePolicyBaseBean {
        // TODO: 2018/3/22 通过RPC获取企业信息
    }

    public static class PersonInfo {

        // '人员类型: 1投保人 2被保人 3受益人',
        public int type;

        // '证件类型（1为身份证，2为护照，3为军官证）'
        public int cardType;
        public String cardTypeText;

        // '证件号'
        public String cardCode;

        // '被保人 投保人的（关系）'
        public String relationName;

        // '被保人姓名'
        public String name;

        // '手机号'
        public String phone;

        // '生日'
        public String birthday;

        // '性别 1 男 2 女 '
        public String sex;
        public String sexText;

        // '邮箱'
        public String email;

        // '被保车牌号'
        public String carCode;

        // '职业'
        public String occupation;

        // '国籍'
        public String nationality;

        // '年收入'
        public String annualIncome;

        // '身高'
        public String height;

        // '体重'
        public String weight;

        // '地区'
        public String area;

        // '详细地址'
        public String address;

        public boolean setCardType(int cardType) {
            if (cardType != CardCodeKit.CARD_TYPE_ID_CARD && cardType != CardCodeKit.CARD_TYPE_PASSPORT && cardType != CardCodeKit.CARD_TYPE_MILITARY_CERTIFICATE) {
                return false;
            }

            this.cardType = cardType;
            setCardTypeText(cardType);

            return true;
        }

        public int cardType() {
            return cardType;
        }

        public void setCardTypeText(int cardType) {
            switch (cardType) {
                case CardCodeKit.CARD_TYPE_ID_CARD:
                    cardTypeText = "身份证";
                    break;
                case CardCodeKit.CARD_TYPE_PASSPORT:
                    cardTypeText = "护照";
                    break;
                case CardCodeKit.CARD_TYPE_MILITARY_CERTIFICATE:
                    cardTypeText = "军官证";
                    break;
            }
        }

        public String getCardTypeText() {
            return cardTypeText;
        }

        // 先设置证件类型
        public boolean setCardCode(String cardCode) {
            if (cardType == 0) {
                return false;
            }

            if (!CardCodeKit.isLegal(cardType, cardCode)) {
                return false;
            }

            this.cardCode = cardCode;

            return true;
        }

        public String getCardCode() {
            return cardCode;
        }

    }

}
