package com.inschos.cloud.trading.model;

import java.util.List;

/**
 * 创建日期：2018/6/25 on 14:17
 * 描述：
 * 作者：zhangyunhe
 */
public class BillModel {

    /**
     * 主键id
     */
    public String id;

    /**
     * 业管uuid
     */
    public String manager_uuid;

    /**
     * 结算单号
     */
    public String bill_uuid;

    /**
     * 结算标识
     */
    public String bill_name;

    /**
     * 保险公司
     */
    public String insurance_company_id;

    /**
     * 负责人
     */
    public String principal;

    /**
     * 结算状态，0-未结算，1-结算
     */
    public String is_settlement;

    /**
     * 备注
     */
    public String remark;

    /**
     * 结算时间
     */
    public String bill_time;

    /**
     * 结算金额
     */
    public String bill_money;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 更新时间
     */
    public String updated_at;

    /**
     * 删除状态
     */
    public String state;

    public Page page;

    public String principal_string;
    public String search;
    public String searchType;
    public String insurance_company_id_string;
    public String insurance_company_name;
    public String principal_name;

    public List<BillDetailModel> billDetailModelList;

    // 已结算
    public static final String SETTLEMENT_STATE_NOT = "0";
    // 未结算
    public static final String SETTLEMENT_STATE_ALREADY = "1";

    public String isSettlementText(String isSettlement) {
        String str = "";
        if (isSettlement == null) {
            return str;
        }
        // 车结算状态，0-未结算，1-结算
        switch (isSettlement) {
            case SETTLEMENT_STATE_NOT:
                str = "未结算";
                break;
            case SETTLEMENT_STATE_ALREADY:
                str = "已结算";
                break;
        }
        return str;
    }

}
