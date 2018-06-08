package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

import java.util.*;

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
    public String pre_policy_no;
    /**
     * 保单号
     */
    public String warranty_code;
    /**
     * 归属账号uuid
     */
    public String manager_uuid;
    /**
     * 买家uuid
     */
    public String account_uuid;
    /**
     * 代理人ID为null则为用户自主购买
     */
    public String agent_id;
    /**
     * 渠道ID为0则为用户自主购买
     */
    public String channel_id;
    /**
     * 计划书ID为0则为用户自主购买
     */
    public String plan_id;
    /**
     * 产品ID
     */
    public String product_id;
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
     * 保单状态 1投保中 2待生效 3保障中 4可续保 5已过保，6已退保
     */
    public String warranty_status;
    /**
     * 积分
     */
    public String integral;
    /**
     * 快递单号
     */
    public String express_no;
    /**
     * 快递公司名称
     */
    public String express_company_name;
    /**
     * 邮寄详细地址
     */
    public String express_address;
    /**
     * 邮寄省级代码
     */
    public String express_province_code;
    /**
     * 邮寄市级代码
     */
    public String express_city_code;
    /**
     * 邮寄地区代码
     */
    public String express_county_code;
    /**
     * 保单发送电子邮箱
     */
    public String express_email;
    /**
     * 快递方式，0-自取，1-快递
     */
    public String delivery_type;
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
    public String searchType;

    public Page page;

    public String status_string;
    public String search_warranty_string;

    public String product_id_string;
    public String agent_id_string;
    public long currentTime = System.currentTimeMillis();
    public List<InsuranceParticipantModel> insured_list;

    // 核保成功
    public static final String APPLY_UNDERWRITING_SUCCESS = "0";
    // 核保中
    public static final String APPLY_UNDERWRITING_PROCESSING = "1";
    // 核保失败
    public static final String APPLY_UNDERWRITING_FAILURE = "2";

    // 未结算
    public static final String COMMISSION_UNSETTLE = "0";
    // 已结算
    public static final String COMMISSION_SETTLED = "1";

    public boolean setIsSettlement(String isSettlement) {
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

    public boolean setWarrantyFrom(String warrantyFrom) {
        if (!StringKit.isInteger(warrantyFrom) || Integer.valueOf(warrantyFrom) > 4 || Integer.valueOf(warrantyFrom) < 1) {
            return false;
        }

        this.warranty_from = warrantyFrom;
        return true;
    }

    // 投保中
    public static final String POLICY_STATUS_PENDING = "1";
    // 待生效
    public static final String POLICY_STATUS_WAITING = "2";
    // 保障中
    public static final String POLICY_STATUS_EFFECTIVE = "3";
    // 可续保
    public static final String POLICY_STATUS_CONTINUE = "4";
    // 已过保
    public static final String POLICY_STATUS_EXPIRED = "5";
    // 已失效
    public static final String POLICY_STATUS_INVALID = "6";

    public boolean setWarrantyStatus(String warrantyStatus) {
        if (!StringKit.isInteger(warrantyStatus) || Integer.valueOf(warrantyStatus) > 6 || Integer.valueOf(warrantyStatus) < 1) {
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

    public static LinkedHashMap<String, String> getWarrantyFromMap() {
        InsurancePolicyModel model = new InsurancePolicyModel();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(SOURCE_SELF, model.warrantyFromText(SOURCE_SELF));
        linkedHashMap.put(SOURCE_ONLINE, model.warrantyFromText(SOURCE_ONLINE));
        linkedHashMap.put(SOURCE_OFFLINE, model.warrantyFromText(SOURCE_OFFLINE));
        linkedHashMap.put(SOURCE_COPY, model.warrantyFromText(SOURCE_COPY));
        return linkedHashMap;
    }

    public String warrantyStatusText(String warrantyStatus) {
        String str = "";
        if (warrantyStatus == null) {
            return str;
        }
        // 1-投保中，2-待生效，3-保障中，4-可续保，5-已过保，6-已失效
        switch (warrantyStatus) {
            case POLICY_STATUS_PENDING:
                str = "投保中";
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
            case POLICY_STATUS_EXPIRED:
                str = "已过保";
                break;
            case POLICY_STATUS_INVALID:
                str = "已失效";
                break;
        }
        return str;
    }

    public static LinkedHashMap<String, String> getWarrantyStatusMap() {
        InsurancePolicyModel model = new InsurancePolicyModel();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(POLICY_STATUS_PENDING, model.warrantyStatusText(POLICY_STATUS_PENDING));
        linkedHashMap.put(POLICY_STATUS_WAITING, model.warrantyStatusText(POLICY_STATUS_WAITING));
        linkedHashMap.put(POLICY_STATUS_EFFECTIVE, model.warrantyStatusText(POLICY_STATUS_EFFECTIVE));
        linkedHashMap.put(POLICY_STATUS_CONTINUE, model.warrantyStatusText(POLICY_STATUS_CONTINUE));
        linkedHashMap.put(POLICY_STATUS_EXPIRED, model.warrantyStatusText(POLICY_STATUS_EXPIRED));
        linkedHashMap.put(POLICY_STATUS_INVALID, model.warrantyStatusText(POLICY_STATUS_INVALID));
        return linkedHashMap;
    }

    public static final String DELIVERY_TYPE_SELF = "0";
    public static final String DELIVERY_TYPE_EXPRESS = "1";

    public String deliveryTypeText(String deliveryType) {
        String str = "";
        if (deliveryType == null) {
            return str;
        }
        // 0-自取，1-快递
        switch (deliveryType) {
            case DELIVERY_TYPE_SELF:
                str = "自取";
                break;
            case DELIVERY_TYPE_EXPRESS:
                str = "快递";
                break;
        }
        return str;
    }

    /**
     * 投保人姓名
     */
    public String policy_holder_name;

    /**
     * 投保人电话
     */
    public String policy_holder_phone;

    /**
     * 流水号
     */
    public String biz_id;

    /**
     * 第三方业务id
     */
    public String thp_biz_id;

    /**
     * 车险类型 1-强险，2-商业险
     */
    public String insurance_type;

    /**
     * 车牌号
     */
    public String car_code;

    /**
     * 车主姓名
     */
    public String name;

    /**
     * 车主证件号
     */
    public String card_code;

    /**
     * 车主证件类型
     */
    public String card_type;

    /**
     * 车主手机号
     */
    public String phone;

    /**
     * 生日
     */
    public String birthday;

    /**
     * 性别
     */
    public String sex;

    /**
     * 年龄
     */
    public String age;

    /**
     * 车架号
     */
    public String frame_no;

    /**
     * 发动机号
     */
    public String engine_no;

    /**
     * 发改委编码
     */
    public String vehicle_fgw_code;

    /**
     * 发改委名称
     */
    public String vehicle_fgw_name;

    /**
     * 品牌型号编码
     */
    public String brand_code;

    /**
     * 品牌名称
     */
    public String brand_name;

    /**
     * 年份款型
     */
    public String parent_veh_name;

    /**
     * 排量
     */
    public String engine_desc;

    /**
     * 投保时是否未上牌（0:否 1:是）
     */
    public String is_not_car_code;

    /**
     * 是否过户车（0:否 1:是）
     */
    public String is_trans;

    /**
     * 过户日期
     */
    public String trans_date;

    /**
     * 初登日期
     */
    public String first_register_date;

    /**
     * 车系名称
     */
    public String family_name;

    /**
     * 车挡类型
     */
    public String gearbox_type;

    /**
     * 备注
     */
    public String car_remark;

    /**
     * 新车购置价
     */
    public String new_car_price;

    /**
     * 含税价格
     */
    public String purchase_price_tax;

    /**
     * 进口标识，0：国产，1：合资，2：进口
     */
    public String import_flag;

    /**
     * 参考价
     */
    public String purchase_price;

    /**
     * 座位数
     */
    public String car_seat;

    /**
     * 款型名称
     */
    public String standard_name;

    /**
     * 险别列表json
     */
    public String coverage_list;

    /**
     * 特约条款json
     */
    public String sp_agreement;

    /**
     * 车险验证码标识
     */
    public String bj_code_flag;

    public static final Map<String,String> CAR_FIELD_MAP;

    static {
        CAR_FIELD_MAP = new HashMap<>();

    }

    public static final Map<String,String> PERSON_FIELD_MAP;

    static {
        PERSON_FIELD_MAP = new HashMap<>();
    }

    public static final Map<String,String> TEAM_FIELD_MAP;

    static {
        TEAM_FIELD_MAP = new HashMap<>();
    }

    public String premium;
    public String pay_status;

    public Set<String> uuids;

}
