package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

/**
 * 创建日期：2018/3/22 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicyModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 投保单号
     */
    public String pro_policy_no;

    /**
     * 保单号
     */
    public String warranty_code;

    /**
     * 归属账号uuid
     */
    public String account_uuid;

    /**
     * 买家uuid
     */
    public String buyer_auuid;

    /**
     * 代理人ID为null则为用户自主购买
     */
    public String agent_auuid;

    /**
     * 渠道ID为0则为用户自主购买
     */
    public String ditch_id;

    /**
     * 计划书ID为0则为用户自主购买
     */
    public String plan_id;

    /**
     * 产品ID
     */
    public String product_id;

    /**
     * 保单价格
     */
    public String premium;

    /**
     * 起保时间
     */
    public String start_time;

    /**
     * 结束时间
     */
    public String end_time;

    /**
     * 保险公司ID
     */
    public String ins_company_id;

    /**
     * 购买份数
     */
    public String count;

    /**
     * 支付时间
     */
    public String pay_time;

    /**
     * 支付方式 1 银联 2 支付宝 3 微信 4现金
     */
    public String pay_way;

    /**
     * 分期方式
     */
    public String by_stages_way;

    /**
     * 佣金 0表示未结算，1表示已结算
     */
    public String is_settlement;

    /**
     * 电子保单下载地址
     */
    public String warranty_url;

    /**
     * 保单来源 1 自购 2线上成交 3线下成交 4导入
     */
    public String warranty_from;

    /**
     * 保单类型1表示个人保单，2表示团险保单，3表示车险保单
     */
    public String type;

    /**
     * 核保状态（01核保中 2核保失败，3核保成功
     */
    public String check_status;

    /**
     * 支付状态 0，1支付中2支付失败3支付成功，
     */
    public String pay_status;

    /**
     * 保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保
     */
    public String warranty_status;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    /**
     * 删除标识 0删除 1可用
     */
    public String state;

    public String search;

    public Page page;

    // 银联
    public static final String PAY_WAY_CHINA_UNION = "1";
    // 支付宝
    public static final String PAY_WAY_ALI = "2";
    // 微信
    public static final String PAY_WAY_WE_CHAT = "3";
    // 现金
    public static final String PAY_WAY_CASH = "4";

    public boolean setPayWay(String payWay) {
        if (!StringKit.isInteger(payWay) || Integer.valueOf(payWay) > 4 || Integer.valueOf(payWay) < 1) {
            return false;
        }

        this.pay_way = payWay;
        return true;
    }

    // 未结算
    public static final String COMMISSION_UNSETTLE = "0";
    // 已结算
    public static final String COMMISSION_SETTLED = "1";

    public boolean setIsSettlement (String isSettlement) {
        if (!StringKit.isInteger(isSettlement) || Integer.valueOf(isSettlement) > 1 || Integer.valueOf(isSettlement) < 0) {
            return false;
        }

        this.is_settlement = isSettlement;
        return true;
    }

    // 自购
    public static final String SOURCE_SELF = "1";
    // 线上成交
    public static final String SOURCE_ONLINE = "2";
    // 线下成交
    public static final String SOURCE_OFFLINE = "3";
    // 导入
    public static final String SOURCE_COPY = "4";

    public boolean setWarrantyFrom (String warrantyFrom) {
        if (!StringKit.isInteger(warrantyFrom) || Integer.valueOf(warrantyFrom) > 4 || Integer.valueOf(warrantyFrom) < 1) {
            return false;
        }

        this.warranty_from = warrantyFrom;
        return true;
    }

    // 核保中
    public static final String APPLY_UNDERWRITING_PROCESSING_0 = "0";
    public static final String APPLY_UNDERWRITING_PROCESSING_1 = "1";
    // 核保失败
    public static final String APPLY_UNDERWRITING_FAILURE = "2";
    // 核保成功
    public static final String APPLY_UNDERWRITING_SUCCESS = "3";

    public boolean setCheckStatus(String checkStatus) {
        if (!StringKit.isInteger(checkStatus) || Integer.valueOf(checkStatus) > 3 || Integer.valueOf(checkStatus) < 0) {
            return false;
        }

        this.check_status = checkStatus;
        return true;
    }

    // 支付中
    public static final String PAY_STATUS_PROCESSING_0 = "0";
    public static final String PAY_STATUS_PROCESSING_1 = "1";
    // 支付失败
    public static final String PAY_STATUS_FAILURE = "2";
    // 支付成功
    public static final String PAY_STATUS_SUCCESS = "3";

    public boolean setPayStatus(String payStatus) {
        if (!StringKit.isInteger(payStatus) || Integer.valueOf(payStatus) > 3 || Integer.valueOf(payStatus) < 0) {
            return false;
        }

        this.pay_status = payStatus;
        return true;
    }

    // 待处理
    public static final String POLICY_STATUS_PENDING = "1";
    // 待支付
    public static final String POLICY_STATUS_PAYING = "2";
    // 待生效
    public static final String POLICY_STATUS_WAITING = "3";
    // 保障中
    public static final String POLICY_STATUS_EFFECTIVE = "4";
    // 可续保
    public static final String POLICY_STATUS_CONTINUE = "5";
    // 已失效
    public static final String POLICY_STATUS_INVALID = "6";
    // 已退保
    public static final String POLICY_STATUS_SURRENDER = "7";
    // 已过保
    public static final String POLICY_STATUS_EXPIRED = "8";

    public boolean setWarrantyStatus(String warrantyStatus) {
        if (!StringKit.isInteger(warrantyStatus) || Integer.valueOf(warrantyStatus) > 8 || Integer.valueOf(warrantyStatus) < 1) {
            return false;
        }

        this.warranty_status = warrantyStatus;
        return true;
    }

    // 个人保单
    public static final String POLICY_TYPE_PERSON = "1";
    // 团险保单
    public static final String POLICY_TYPE_TEAM = "2";
    // 车险保单
    public static final String POLICY_TYPE_CAR = "3";

    public boolean setType(String type) {
        if (!StringKit.isInteger(type) || Integer.valueOf(type) > 3 || Integer.valueOf(type) < 1) {
            return false;
        }

        this.type = type;
        return true;
    }

    public String payWayText(String payWay) {
        String str = "";
        if (payWay == null) {
            return str;
        }
        // 1 银联 2 支付宝 3 微信 4 现金
        switch (payWay) {
            case PAY_WAY_CHINA_UNION:
                str = "银联";
                break;
            case PAY_WAY_ALI:
                str = "支付宝";
                break;
            case PAY_WAY_WE_CHAT:
                str = "微信";
                break;
            case PAY_WAY_CASH:
                str = "现金";
                break;
        }
        return str;
    }

    public String isSettlementText(String isSettlement) {
        String str = "";
        if (isSettlement == null) {
            return str;
        }
        //佣金 0-未结算，1-已结算
        switch (isSettlement) {
            case COMMISSION_UNSETTLE:
                str = "未结算";
                break;
            case COMMISSION_SETTLED:
                str = "已结算";
                break;
        }
        return str;
    }

    public String warrantyFromText(String warrantyFrom) {
        String str = "";
        if (warrantyFrom == null) {
            return str;
        }
        // 1-自购 2-线上成交 3-线下成交 4-导入
        switch (warrantyFrom) {
            case SOURCE_SELF:
                str = "自购";
                break;
            case SOURCE_ONLINE:
                str = "线上成交";
                break;
            case SOURCE_OFFLINE:
                str = "线下成交";
                break;
            case SOURCE_COPY:
                str = "导入";
                break;
        }
        return str;
    }

    public String checkStatusText(String checkStatus) {
        String str = "";
        if (checkStatus == null) {
            return str;
        }
        // 核保状态 01-核保中 2-核保失败，3-核保成功
        switch (checkStatus) {
            case APPLY_UNDERWRITING_PROCESSING_0:
            case APPLY_UNDERWRITING_PROCESSING_1:
                str = "核保中";
                break;
            case APPLY_UNDERWRITING_FAILURE:
                str = "核保失败";
                break;
            case APPLY_UNDERWRITING_SUCCESS:
                str = "核保成功";
                break;
        }
        return str;
    }

    public String payStatusText(String payStatus) {
        String str = "";
        if (payStatus == null) {
            return str;
        }
        // 支付状态 0，1-支付中 2-支付失败 3-支付成功
        switch (payStatus) {
            case PAY_STATUS_PROCESSING_0:
            case PAY_STATUS_PROCESSING_1:
                str = "支付中";
                break;
            case PAY_STATUS_FAILURE:
                str = "支付失败";
                break;
            case PAY_STATUS_SUCCESS:
                str = "支付成功";
                break;
        }
        return str;
    }

    public String warrantyStatusText(String warrantyStatus) {
        String str = "";
        if (warrantyStatus == null) {
            return str;
        }
        // 1-待处理 2-待支付 3-待生效 4-保障中 5-可续保 6-已失效 7-已退保 8-已过保
        switch (warrantyStatus) {
            case POLICY_STATUS_PENDING:
                str = "待处理";
                break;
            case POLICY_STATUS_PAYING:
                str = "待支付";
                break;
            case POLICY_STATUS_WAITING:
                str = "待生效";
                break;
            case POLICY_STATUS_EFFECTIVE:
                str = "保障中";
                break;
            case POLICY_STATUS_CONTINUE:
                str = "可续保";
                break;
            case POLICY_STATUS_INVALID:
                str = "已失效";
                break;
            case POLICY_STATUS_SURRENDER:
                str = "已退保";
                break;
            case POLICY_STATUS_EXPIRED:
                str = "已过保";
                break;
        }
        return str;
    }

}
