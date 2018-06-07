package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.access.http.controller.action.CarInsuranceAction;
import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;
import com.inschos.cloud.trading.model.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 创建日期：2018/3/22 on 14:03
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicy {

    public static class GetInsurancePolicyListForOnlineStoreRequest extends BaseRequest {
        // 保单状态
        public String warrantyStatus;
        // 搜索关键字
        public String searchKey;
    }

    public static class GetInsurancePolicyListForOnlineStoreResponse extends BaseResponse {
        public List<GetInsurancePolicy> data;
    }

    public static class GetInsurancePolicy {

        public String id;

        //内部保单唯一标识
        public String warrantyUuid;

        //投保单号
        public String prePolicyNo;

        //保单号
        public String warrantyCode;

        //归属账号uuid
        public String accountUuid;

        //买家uuid
        public String buyerAuuid;

        //代理人ID为null则为用户自主购买
        public String agentId;
        public String agentName;

        //渠道ID为0则为用户自主购买
        public String channelId;

        //计划书ID为0则为用户自主购买
        public String planId;

        //产品ID
        public String productId;
        public String productName;

        //保单价格
        public String premium;

        //保单价格（显示用）
        public String premiumText;

        //实际支付金额
        public String payMoney;

        //实际支付金额（显示用）
        public String payMoneyText;

        //税费
        public String taxMoney;

        //税费（显示用）
        public String taxMoneyText;

        //起保时间
        public String startTime;

        //起保时间（显示用）
        public String startTimeText;

        //结束时间
        public String endTime;

        //结束时间（显示用）
        public String endTimeText;

        //保障区间
        public String term;

        //保险公司ID
        public String insCompanyId;

        //购买份数
        public String count;

        //分期方式
        public String byStagesWay;

        //佣金 0表示未结算，1表示已结算
        public String isSettlement;

        //佣金 0表示未结算，1表示已结算（显示用）
        public String isSettlementText;
        public String brokerage;
        public String brokerageText;

        //电子保单下载地址
        public String warrantyUrl;

        //保单来源 1 自购 2线上成交 3线下成交 4导入
        public String warrantyFrom;

        //保单来源 1 自购 2线上成交 3线下成交 4导入（显示用）
        public String warrantyFromText;

        //保单类型1表示个人保单，2表示团险保单，3表示车险保单
        public String type;

        //保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保
        public String warrantyStatus;
        public String payStatus;

        //保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保（显示用）
        public String warrantyStatusText;

        // 积分
        public String integral;

        public String expressEmail;
        public String expressAddress;
        public String expressProvinceCode;
        public String expressCityCode;
        public String expressCountyCode;

        // 快递单号
        public String expressNo;

        // 快递公司名称
        public String expressCompanyName;

        // 快递方式，0-自取，1-快递
        public String deliveryType;
        public String deliveryTypeText;

        //下单时间
        public String createdAt;

        //下单时间（显示用）
        public String createdAtText;

        //更新时间
        public String updatedAt;

        //更新时间（显示用）
        public String updatedAtText;

        // 被保险人
        public String insuredText;

        public String insuranceProductName;

        public String insuranceCompanyName;
        public String insuranceCompanyLogo;

        // 车险用验证码（仅车险存在）
        public String bjCodeFlag;
        // 车险流水号
        public String bizId;
        public String insuranceClaimsCount = "0";
        public String groupName;

        public List<CustWarrantyBrokerage> brokerageList;

        public GetInsurancePolicy() {

        }

        public GetInsurancePolicy(InsurancePolicyModel model, BigDecimal premium, BigDecimal pay_money, BigDecimal tax_money, String warrantyStatusForPay, String warrantyStatusForPayText) {
            if (model == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
            SimpleDateFormat group = new SimpleDateFormat("yyyy-MM");
            this.id = model.id;
            this.warrantyUuid = model.warranty_uuid;
            this.prePolicyNo = model.pre_policy_no;
            this.warrantyCode = model.warranty_code;
            this.accountUuid = model.manager_uuid;
            this.buyerAuuid = model.account_uuid;
            this.agentId = model.agent_id;
            this.channelId = model.channel_id;
            this.planId = model.plan_id;
            this.productId = model.product_id;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            this.premium = decimalFormat.format(premium.doubleValue());
            this.premiumText = "¥" + this.premium;
            this.payMoney = decimalFormat.format(pay_money.doubleValue());
            this.payMoneyText = "¥" + this.payMoney;
            this.taxMoney = decimalFormat.format(tax_money.doubleValue());
            this.taxMoneyText = "¥" + this.taxMoney;
            this.startTime = model.start_time;
            String start = "";
            if (StringKit.isInteger(model.start_time)) {
                this.startTimeText = sdf.format(new Date(Long.valueOf(model.start_time)));
                start = sdf2.format(new Date(Long.valueOf(model.start_time)));
            }
            this.endTime = model.end_time;
            String end = "";
            if (StringKit.isInteger(model.end_time)) {
                this.endTimeText = sdf.format(new Date(Long.valueOf(model.end_time)));
                end = sdf2.format(new Date(Long.valueOf(model.end_time)));
            }
            this.term = start + "-" + end;
            this.insCompanyId = model.ins_company_id;
            this.count = model.count;
            this.byStagesWay = model.by_stages_way;
            this.isSettlement = model.is_settlement;
            this.isSettlementText = model.isSettlementText(isSettlement);
            this.warrantyUrl = model.warranty_url;
            this.warrantyFrom = model.warranty_from;
            this.warrantyFromText = model.warrantyFromText(warrantyFrom);
            this.type = model.type;
            this.integral = model.integral;
            this.warrantyStatus = model.warranty_status;
            this.payStatus = warrantyStatusForPay;
            if (StringKit.equals(this.warrantyStatus, InsurancePolicyModel.POLICY_STATUS_PENDING)) {
                this.warrantyStatusText = warrantyStatusForPayText;
            } else {
                this.warrantyStatusText = model.warrantyStatusText(warrantyStatus);
            }
            this.expressNo = model.express_no;
            this.expressCompanyName = model.express_company_name;
            this.deliveryType = model.delivery_type;
            this.deliveryTypeText = model.deliveryTypeText(deliveryType);
            this.createdAt = model.created_at;
            if (StringKit.isInteger(model.created_at)) {
                Date date = new Date(Long.valueOf(model.created_at));
                this.createdAtText = sdf.format(date);
                this.groupName = group.format(date);
            }
            this.updatedAt = model.updated_at;
            if (StringKit.isInteger(model.updated_at)) {
                this.updatedAtText = sdf.format(new Date(Long.valueOf(model.updated_at)));
            }


            this.expressEmail = model.express_email;
            this.expressAddress = model.express_address;
            this.expressProvinceCode = model.express_province_code;
            this.expressCityCode = model.express_city_code;
            this.expressCountyCode = model.express_county_code;
        }

    }

    public static class GetInsurancePolicyDetailForOnlineStoreRequestRequest extends BaseRequest {
        // 保单唯一id
        @CheckParams(hintName = "保单id")
        public String warrantyUuid;
    }

    public static class GetInsurancePolicyDetailForOnlineStoreRequestResponse extends BaseResponse {
        public GetInsurancePolicyDetail data;
    }

    public static class GetInsurancePolicyDetail extends GetInsurancePolicy {
        // 投保人
        public InsurancePolicyParticipantInfo policyHolder;
        // 被保险人
        public List<InsurancePolicyParticipantInfo> insuredList;
        // 受益人
        public List<InsurancePolicyParticipantInfo> beneficiaryList;
        // 车辆信息（仅车险有此信息）
        public CarInfo carInfo;

        public GetInsurancePolicyDetail() {
            super();
        }

        public GetInsurancePolicyDetail(InsurancePolicyModel model, BigDecimal premium, BigDecimal pay_money, BigDecimal tax_money, String warrantyStatusForPay, String warrantyStatusForPayText) {
            super(model, premium, pay_money, tax_money, warrantyStatusForPay, warrantyStatusForPayText);
        }
    }

    public static class CarInfo {

        //主键
        public String id;

        //内部保单唯一标识
        public String warrantyUuid;

        //流水号
        public String bizId;

        //第三方业务id
        public String thpBizId;

        //车险类型 1-强险，2-商业险
        public String insuranceType;
        public String insuranceTypeText;

        //车牌号
        public String carCode;

        //车主姓名
        public String name;

        //车主身份证号
        public String cardCode;

        //证件类型，1为身份证，2为护照，3为军官证
        public String cardType;
        public String cardTypeText;

        //车主手机号
        public String phone;

        //车主生日时间戳
        public String birthday;
        public String birthdayText;

        //车主性别 1-男，2-女
        public String sex;
        public String sexText;

        //
        public String age;

        //车架号
        public String frameNo;

        //发动机号
        public String engineNo;

        //发改委编码
        public String vehicleFgwCode;

        //发改委名称
        public String vehicleFgwName;

        //品牌型号编码
        public String brandCode;

        //品牌名称
        public String brandName;

        //年份款型
        public String parentVehName;

        //排量
        public String engineDesc;

        //投保时是否未上牌（0:否 1:是）
        public String isNotCarCode;

        //是否过户车（0:否 1:是）
        public String isTrans;

        //过户日期
        public String transDate;
        public String transDateText;

        //初登日期
        public String firstRegisterDate;
        public String firstRegisterDateText;

        //车系名称
        public String familyName;

        //车挡类型
        public String gearboxType;

        //备注
        public String carRemark;

        //新车购置价
        public String newCarPrice;
        public String newCarPriceText;

        //含税价格
        public String purchasePriceTax;
        public String purchasePriceTaxText;

        //进口标识，0：国产，1：合资，2：进口
        public String importFlag;
        public String importFlagText;

        //参考价
        public String purchasePrice;
        public String purchasePriceText;

        //座位数
        public String carSeat;

        //款型名称
        public String standardName;

        //险别列表json
        // public String coverageList;
        public List<CarInsurance.InsuranceInfo> coverageList;

        // 特约信息
        // public String spAgreement;
        public List<ExtendCarInsurancePolicy.SpAgreement> spAgreement;

        // 车险验证码标识
        public String bjCodeFlag;

        //结束时间
        public String updatedAt;
        public String updatedAtText;

        public CarInfo() {

        }

        public CarInfo(CarInfoModel model) {
            if (model == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.id = model.id;
            this.warrantyUuid = model.warranty_uuid;
            this.bizId = model.biz_id;
            this.thpBizId = model.thp_biz_id;
            this.insuranceType = model.insurance_type;
            this.insuranceTypeText = model.insuranceTypeText(model.insurance_type);
            this.carCode = model.car_code;
            this.name = model.name;
            this.cardCode = model.card_code;
            this.cardType = model.card_type;
            this.cardTypeText = model.cardTypeText(model.card_type);
            this.phone = model.phone;
            this.birthday = model.birthday;
            if (StringKit.isInteger(model.birthday)) {
                this.birthdayText = sdf.format(new Date(Long.valueOf(model.birthday)));
            }
            this.sex = model.sex;
            this.sexText = new InsuranceParticipantModel().sexText(model.sex);
            this.age = model.age;
            this.frameNo = model.frame_no;
            this.engineNo = model.engine_no;
            this.vehicleFgwCode = model.vehicle_fgw_code;
            this.vehicleFgwName = model.vehicle_fgw_name;
            this.brandCode = model.brand_code;
            this.brandName = model.brand_name;
            this.parentVehName = model.parent_veh_name;
            this.engineDesc = model.engine_desc;
            this.isNotCarCode = model.is_not_car_code;
            this.isTrans = model.is_trans;
            this.transDate = model.trans_date;
            if (StringKit.isInteger(model.trans_date)) {
                this.transDateText = sdf.format(new Date(Long.valueOf(model.trans_date)));
            }
            this.firstRegisterDate = model.first_register_date;
            if (StringKit.isInteger(model.first_register_date)) {
                this.firstRegisterDateText = sdf.format(new Date(Long.valueOf(model.first_register_date)));
            }
            this.familyName = model.family_name;
            this.gearboxType = model.gearbox_type;
            this.carRemark = model.car_remark;
            this.newCarPrice = model.new_car_price;
            if (StringKit.isEmpty(model.new_car_price)) {
                this.newCarPriceText = "¥0.00";
            } else {
                this.newCarPriceText = "¥" + model.new_car_price;
            }
            this.purchasePriceTax = model.purchase_price_tax;
            if (StringKit.isEmpty(model.purchase_price_tax)) {
                this.purchasePriceTaxText = "¥0.00";
            } else {
                this.purchasePriceTaxText = "¥" + model.purchase_price_tax;
            }
            this.importFlag = model.import_flag;
            this.importFlagText = model.importFlagText(model.import_flag);
            this.purchasePrice = model.purchase_price;
            if (StringKit.isEmpty(model.purchase_price)) {
                this.purchasePriceText = "¥0.00";
            } else {
                this.purchasePriceText = "¥" + model.purchase_price;
            }
            this.carSeat = model.car_seat;
            this.standardName = model.standard_name;
            List<CarInsurance.InsuranceInfo> list = model.parseCoverageList(model.coverage_list);
            this.coverageList = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (CarInsurance.InsuranceInfo o : list) {
                    if (model.insuranceType(insuranceType, o.coverageCode)) {
                        // StringKit.equals(o.hasExcessOption, "1") &&
                        if (StringKit.equals(o.isExcessOption, "1")) {
                            o.coverageName = String.format("%s（不计免赔）", o.coverageName);
                        }
                        this.coverageList.add(o);
                    }

                    o.insuredAmountText = "";
                    if (StringKit.isNumeric(o.insuredAmount)) {
                        o.insuredAmountText = "¥" + o.insuredAmount;
                    }

                    if (StringKit.equals(o.coverageCode, "F")) {
                        CarInsuranceAction.CheckCoverageListResult coverageListResult = new CarInsuranceAction.CheckCoverageListResult();
                        o.insuredAmountText = coverageListResult.getFText(o.flag);
                    } else if (StringKit.equals(o.coverageCode, "Z2")) {
                        o.insuredAmountText = o.amount + "元/天 × " + o.day + "天";
                    }
                }
            }
            this.spAgreement = model.parseSpAgreement(model.sp_agreement);
            this.bjCodeFlag = model.bj_code_flag;
            this.updatedAt = model.updated_at;
            if (StringKit.isInteger(model.updated_at)) {
                this.updatedAtText = sdf.format(new Date(Long.valueOf(model.updated_at)));
            }
        }

    }

    public static class InsurancePolicyParticipantInfo {

        //内部保单唯一标识
        public String warrantyUuid;

        //人员类型: 1投保人 2被保人 3受益人
        public String type;
        public String typeText;

        //被保人 投保人的（关系）
        public String relationName;

        //被保人单号
        public String outOrderNo;

        //姓名
        public String name;

        //证件类型（1为身份证，2为护照，3为军官证）
        public String cardType;
        public String cardTypeText;

        //证件号
        public String cardCode;

        //手机号
        public String phone;

        //职业
        public String occupation;

        //生日
        public String birthday;
        public String birthdayText;

        //性别 1 男 2 女
        public String sex;
        public String sexText;

        //年龄
        public String age;

        //邮箱
        public String email;

        //国籍
        public String nationality;

        //年收入
        public String annualIncome;

        //身高
        public String height;

        //体重
        public String weight;

        //地区
        public String area;

        //详细地址
        public String address;

        //开始时间
        public String startTime;
        public String startTimeText;

        //结束时间
        public String endTime;
        public String endTimeText;

        public InsurancePolicyParticipantInfo() {

        }

        public InsurancePolicyParticipantInfo(InsuranceParticipantModel model) {
            if (model == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.warrantyUuid = model.warranty_uuid;
            this.type = model.type;
            this.relationName = model.relation_name;
            this.outOrderNo = model.out_order_no;
            this.name = model.name;
            this.cardType = model.card_type;
            this.cardTypeText = model.cardTypeText(model.card_type);
            this.cardCode = model.card_code;
            this.phone = model.phone;
            this.occupation = model.occupation;
            this.birthday = model.birthday;
            if (StringKit.isInteger(model.birthday)) {
                this.birthdayText = sdf.format(new Date(Long.valueOf(model.birthday)));
            }
            this.sex = model.sex;
            this.sexText = model.sexText(model.sex);
            this.age = model.age;
            this.email = model.email;
            this.nationality = model.nationality;
            this.annualIncome = model.annual_income;
            this.height = model.height;
            this.weight = model.weight;
            this.area = model.area;
            this.address = model.address;
            this.startTime = model.start_time;
            if (StringKit.isInteger(model.start_time)) {
                this.startTimeText = sdf.format(new Date(Long.valueOf(model.start_time)));
            }
            this.endTime = model.end_time;
            if (StringKit.isInteger(model.end_time)) {
                this.endTimeText = sdf.format(new Date(Long.valueOf(model.end_time)));
            }
        }
    }

    public static class GetInsurancePolicyStatusListRequest extends BaseRequest {

    }

    public static class GetInsurancePolicyStatusListResponse extends BaseResponse {
        public List<GetInsurancePolicyStatus> data;
    }

    public static class GetInsurancePolicySourceListRequest extends BaseRequest {

    }

    public static class GetInsurancePolicySourceListResponse extends BaseResponse {
        public List<GetInsurancePolicyStatus> data;
    }

    public static class GetInsurancePolicyStatus {
        public String value;
        public String valueText;
    }

    public static class GetInsurancePolicyListForManagerSystemRequest extends BaseRequest {
        // 保单状态
        public String warrantyStatus;
        // 搜索关键字
        public String searchKey;
        // 关键字字段类型
        public String searchType;
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;
        // 保单类型
        @CheckParams
        public String warrantyType;
    }

    public static class GetInsurancePolicyListForManagerSystemResponse extends BaseResponse {
        public List<GetInsurancePolicyForManagerSystem> data;
    }

    public static class DownloadInsurancePolicyListForManagerSystemResponse extends BaseResponse {
        public String data;
    }

    public static class GetInsurancePolicyForManagerSystem extends GetInsurancePolicy {
        public String contactsName;
        public String contactsMobile;
        public String policyHolderName;
        public String policyHolderMobile;
        public String frameNo;
        public String carCode;

        public GetInsurancePolicyForManagerSystem() {

        }

        public GetInsurancePolicyForManagerSystem(InsurancePolicyModel model, BigDecimal premium, BigDecimal pay_money, BigDecimal tax_money, String warrantyStatusForPay, String warrantyStatusForPayText) {
            super(model, premium, pay_money, tax_money, warrantyStatusForPay, warrantyStatusForPayText);
        }

        public static GetInsurancePolicyForManagerSystem getCarInsurancePolicyTitle() {
            GetInsurancePolicyForManagerSystem insurancePolicy = new GetInsurancePolicyForManagerSystem();

            insurancePolicy.prePolicyNo = "投保单号";
            insurancePolicy.warrantyCode = "保单号";
            insurancePolicy.productName = "保险产品";
            insurancePolicy.policyHolderName = "投保人";
            insurancePolicy.policyHolderMobile = "投保人电话";
            insurancePolicy.carCode = "车牌号";
            insurancePolicy.premiumText = "保费（元）";
            insurancePolicy.createdAtText = "下单时间";
            insurancePolicy.warrantyStatusText = "保单状态";

            return insurancePolicy;
        }

        @Override
        public String toString() {
            return "GetInsurancePolicyForManagerSystem{" +
                    "id='" + id + '\'' +
                    ", warrantyUuid='" + warrantyUuid + '\'' +
                    ", prePolicyNo='" + prePolicyNo + '\'' +
                    ", warrantyCode='" + warrantyCode + '\'' +
                    ", accountUuid='" + accountUuid + '\'' +
                    ", buyerAuuid='" + buyerAuuid + '\'' +
                    ", agentId='" + agentId + '\'' +
                    ", agentName='" + agentName + '\'' +
                    ", channelId='" + channelId + '\'' +
                    ", planId='" + planId + '\'' +
                    ", productId='" + productId + '\'' +
                    ", productName='" + productName + '\'' +
                    ", premium='" + premium + '\'' +
                    ", premiumText='" + premiumText + '\'' +
                    ", payMoney='" + payMoney + '\'' +
                    ", payMoneyText='" + payMoneyText + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", startTimeText='" + startTimeText + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", endTimeText='" + endTimeText + '\'' +
                    ", term='" + term + '\'' +
                    ", insCompanyId='" + insCompanyId + '\'' +
                    ", count='" + count + '\'' +
                    ", byStagesWay='" + byStagesWay + '\'' +
                    ", isSettlement='" + isSettlement + '\'' +
                    ", isSettlementText='" + isSettlementText + '\'' +
                    ", brokerage='" + brokerage + '\'' +
                    ", brokerageText='" + brokerageText + '\'' +
                    ", warrantyUrl='" + warrantyUrl + '\'' +
                    ", warrantyFrom='" + warrantyFrom + '\'' +
                    ", warrantyFromText='" + warrantyFromText + '\'' +
                    ", type='" + type + '\'' +
                    ", warrantyStatus='" + warrantyStatus + '\'' +
                    ", payStatus='" + payStatus + '\'' +
                    ", warrantyStatusText='" + warrantyStatusText + '\'' +
                    ", integral='" + integral + '\'' +
                    ", expressNo='" + expressNo + '\'' +
                    ", expressCompanyName='" + expressCompanyName + '\'' +
                    ", deliveryType='" + deliveryType + '\'' +
                    ", deliveryTypeText='" + deliveryTypeText + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    ", updatedAtText='" + updatedAtText + '\'' +
                    ", insuredText='" + insuredText + '\'' +
                    ", insuranceProductName='" + insuranceProductName + '\'' +
                    ", insuranceCompanyName='" + insuranceCompanyName + '\'' +
                    ", insuranceCompanyLogo='" + insuranceCompanyLogo + '\'' +
                    ", bjCodeFlag='" + bjCodeFlag + '\'' +
                    ", bizId='" + bizId + '\'' +
                    ", insuranceClaimsCount='" + insuranceClaimsCount + '\'' +
                    ", contactsName='" + contactsName + '\'' +
                    ", contactsMobile='" + contactsMobile + '\'' +
                    ", policyHolderName='" + policyHolderName + '\'' +
                    ", policyHolderMobile='" + policyHolderMobile + '\'' +
                    ", frameNo='" + frameNo + '\'' +
                    ", carCode='" + carCode + '\'' +
                    '}';
        }
    }

    public static final Map<String, String> CAR_FIELD_MAP;

    static {
        CAR_FIELD_MAP = new HashMap<>();
        CAR_FIELD_MAP.put("A", "prePolicyNo");
        CAR_FIELD_MAP.put("B", "warrantyCode");
        CAR_FIELD_MAP.put("C", "productName");
        CAR_FIELD_MAP.put("D", "policyHolderName");
        CAR_FIELD_MAP.put("E", "policyHolderMobile");
        CAR_FIELD_MAP.put("F", "carCode");
        CAR_FIELD_MAP.put("G", "premiumText");
        CAR_FIELD_MAP.put("H", "createdAtText");
        CAR_FIELD_MAP.put("I", "warrantyStatusText");
    }

    public static final Map<String, String> PERSON_FIELD_MAP;

    static {
        PERSON_FIELD_MAP = new HashMap<>();
    }

    public static final Map<String, String> TEAM_FIELD_MAP;

    static {
        TEAM_FIELD_MAP = new HashMap<>();
    }

    public static class GetInsurancePolicyDetailForManagerSystemRequest extends BaseRequest {
        @CheckParams
        public String warrantyUuid;
    }

    public static class GetInsurancePolicyDetailForManagerSystemResponse extends GetInsurancePolicyDetailForOnlineStoreRequestResponse {

    }

    public static class GetInsurancePolicyStatisticForManagerSystemRequest extends BaseRequest {
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;
        // 保单渠道
        public String channelId;
    }

    public static class GetInsurancePolicyStatisticForManagerSystemResponse extends BaseResponse {
        public InsurancePolicyStatistic data;
    }

    public static class InsurancePolicyStatistic {
        public String dayAmount;
        public String monthAmount;
        public String totalAmount;
    }

    public static class GetInsurancePolicyStatisticDetailForManagerSystemRequest extends BaseRequest {
        // 时间范围类型，1-今日，2-本月，3-本年
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String timeRangeType;

    }

    public static class GetInsurancePolicyStatisticDetailForManagerSystemResponse extends BaseResponse {
        public InsurancePolicyStatisticDetail data;
    }

    public static class InsurancePolicyStatisticDetail {
        public String startTime;
        public String startTimeText;
        public String endTime;
        public String endTimeText;

        public String insurancePolicyCount;
        public String premium;
        public String premiumText;
        public String brokerage;
        public String brokerageText;
        public String brokeragePercentage;
        public String brokeragePercentageText;
        public String averagePremium;
        public String averagePremiumText;

        public List<InsurancePolicyStatisticItem> insurancePolicyList;
    }

    public static class InsurancePolicyStatisticItem {
        public String timeText;
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

        public InsurancePolicyStatisticItem() {

        }

        public InsurancePolicyStatisticItem(String timeText) {
            this.timeText = timeText;
        }

        public void setPremiumStatisticModel(PremiumStatisticModel premiumStatisticModel) {
            if (premiumStatisticModel == null) {
                return;
            }

            if (!StringKit.isEmpty(premiumStatisticModel.premium) && StringKit.isNumeric(premiumStatisticModel.premium)) {
                this.premium = premiumStatisticModel.premium;
                this.premiumText = "¥" + this.premium;
            } else {
                this.premium = "0.00";
                this.premiumText = "¥0.00";
            }

            if (!StringKit.isEmpty(premiumStatisticModel.insurance_policy_count) && StringKit.isInteger(premiumStatisticModel.insurance_policy_count)) {
                this.insurancePolicyCount = premiumStatisticModel.insurance_policy_count;
            } else {
                this.insurancePolicyCount = "0";
            }

        }

        public void setBrokerageStatisticModel(BrokerageStatisticModel brokerageStatisticModel) {
            if (brokerageStatisticModel == null) {
                return;
            }

            if (!StringKit.isEmpty(brokerageStatisticModel.brokerage) && StringKit.isNumeric(brokerageStatisticModel.brokerage)) {
                this.brokerage = brokerageStatisticModel.brokerage;
                this.brokerageText = "¥" + this.brokerage;
            } else {
                this.brokerage = "0.00";
                this.brokerageText = "¥0.00";
            }
        }
    }

    public static class GetInsurancePolicyBrokerageStatisticListRequest extends BaseRequest {
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;

        public String searchKey;

    }

    public static class GetInsurancePolicyBrokerageStatisticListResponse extends BaseResponse {
        public List<InsurancePolicyBrokerageStatistic> data;
    }

    public static class InsurancePolicyBrokerageStatistic {

        public String costId;
        public String warrantyId;
        public String warrantyUuid;
        public String warrantyCode;
        public String productId;
        public String insCompanyId;
        public String count;
        public String byStagesWay;
        public String warrantyStatus;
        public String phase;
        public String premium;
        public String premiumText;
        public String actualPayTime;
        public String actualPayTimeText;
        public String brokerageId;

        public String warrantyMoney;
        public String warrantyMoneyText;
        public String managerMoney;
        public String managerMoneyText;
        public String channelMoney;
        public String channelMoneyText;
        public String agentMoney;
        public String agentMoneyText;

        public String warrantyRate;
        public String warrantyRateText;
        public String managerRate;
        public String managerRateText;
        public String channelRate;
        public String channelRateText;
        public String agentRate;
        public String agentRateText;

        public String insuranceName;
        public String productName;
        public String customerName;
        public String customerMobile;


        public InsurancePolicyBrokerageStatistic() {

        }

        public InsurancePolicyBrokerageStatistic(BrokerageStatisticListModel brokerageStatisticListModel) {
            if (brokerageStatisticListModel == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            DecimalFormat money = new DecimalFormat("###,###,###,###,##0.00");

            this.costId = brokerageStatisticListModel.cost_id;
            this.warrantyId = brokerageStatisticListModel.warranty_id;
            this.warrantyUuid = brokerageStatisticListModel.warranty_uuid;
            this.warrantyCode = brokerageStatisticListModel.warranty_code;
            this.productId = brokerageStatisticListModel.product_id;
            this.insCompanyId = brokerageStatisticListModel.ins_company_id;

            this.count = brokerageStatisticListModel.count;
            this.byStagesWay = brokerageStatisticListModel.by_stages_way;

            this.warrantyStatus = brokerageStatisticListModel.warranty_status;
            this.phase = brokerageStatisticListModel.phase;

            if (!StringKit.isEmpty(brokerageStatisticListModel.premium) && StringKit.isNumeric(brokerageStatisticListModel.premium)) {
                this.premium = decimalFormat.format(new BigDecimal(brokerageStatisticListModel.premium).doubleValue());
                this.premiumText = "¥" + this.premium;
            } else {
                this.premium = "0.00";
                this.premiumText = "¥0.00";
            }

            this.actualPayTime = brokerageStatisticListModel.actual_pay_time;
            if (StringKit.isInteger(brokerageStatisticListModel.actual_pay_time)) {
                this.actualPayTimeText = sdf.format(new Date(Long.valueOf(brokerageStatisticListModel.actual_pay_time)));
            }

            this.brokerageId = brokerageStatisticListModel.brokerage_id;

            if (StringKit.isNumeric(brokerageStatisticListModel.warranty_money)) {
                this.warrantyMoney = decimalFormat.format(new BigDecimal(brokerageStatisticListModel.warranty_money).doubleValue());
                this.warrantyMoneyText = "¥" + money.format(new BigDecimal(this.warrantyMoney));
            } else {
                this.warrantyMoney = "0.00";
                this.warrantyMoneyText = "¥0.00";
            }

            if (StringKit.isNumeric(brokerageStatisticListModel.manager_money)) {
                this.managerMoney = decimalFormat.format(new BigDecimal(brokerageStatisticListModel.manager_money).doubleValue());
                this.managerMoneyText = "¥" + money.format(new BigDecimal(this.managerMoney));
            } else {
                this.managerMoney = "0.00";
                this.managerMoneyText = "¥0.00";
            }

            if (StringKit.isNumeric(brokerageStatisticListModel.channel_money)) {
                this.channelMoney = decimalFormat.format(new BigDecimal(brokerageStatisticListModel.channel_money).doubleValue());
                this.channelMoneyText = "¥" + money.format(new BigDecimal(this.channelMoney));
            } else {
                this.channelMoney = "0.00";
                this.channelMoneyText = "¥0.00";
            }

            if (StringKit.isNumeric(brokerageStatisticListModel.agent_money)) {
                this.agentMoney = decimalFormat.format(new BigDecimal(brokerageStatisticListModel.agent_money).doubleValue());
                this.agentMoneyText = "¥" + money.format(new BigDecimal(this.agentMoney));
            } else {
                this.agentMoney = "0.00";
                this.agentMoneyText = "¥0.00";
            }

            if (StringKit.isNumeric(brokerageStatisticListModel.warranty_rate)) {
                BigDecimal bigDecimal = new BigDecimal(brokerageStatisticListModel.warranty_rate);
                this.warrantyRate = decimalFormat.format(bigDecimal.doubleValue());
                bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                this.warrantyRateText = decimalFormat.format(bigDecimal.doubleValue()) + "%";
            } else {
                this.warrantyRate = "0.0";
                this.warrantyRateText = "0.00%";
            }

            if (StringKit.isNumeric(brokerageStatisticListModel.manager_rate)) {
                BigDecimal bigDecimal = new BigDecimal(brokerageStatisticListModel.manager_rate);
                this.managerRate = decimalFormat.format(bigDecimal.doubleValue());
                bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                this.managerRateText = decimalFormat.format(bigDecimal.doubleValue()) + "%";
            } else {
                this.managerRate = "0.0";
                this.managerRateText = "0.00%";
            }


            if (StringKit.isNumeric(brokerageStatisticListModel.channel_rate)) {
                BigDecimal bigDecimal = new BigDecimal(brokerageStatisticListModel.channel_rate);
                this.channelRate = decimalFormat.format(bigDecimal.doubleValue());
                bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                this.channelRateText = decimalFormat.format(bigDecimal.doubleValue()) + "%";
            } else {
                this.channelRate = "0.0";
                this.channelRateText = "0.00%";
            }


            if (StringKit.isNumeric(brokerageStatisticListModel.agent_rate)) {
                BigDecimal bigDecimal = new BigDecimal(brokerageStatisticListModel.agent_rate);
                this.agentRate = decimalFormat.format(bigDecimal.doubleValue());
                bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                this.agentRateText = decimalFormat.format(bigDecimal.doubleValue()) + "%";
            } else {
                this.agentRate = "0.0";
                this.agentRateText = "0.00%";
            }

        }

    }

    public static class OfflineInsurancePolicyInputRequest extends BaseRequest {
        @CheckParams(hintName = "文件")
        public String fileKey;
    }

    public static class OfflineInsurancePolicyInputResponse extends BaseResponse {
        public OfflineInsurancePolicyDetail data;
    }

    public static class OfflineInsurancePolicyDetail {
        public String excelFileKey;
        public String excelFileUrl;
        public List<OfflineInsurancePolicy> list;
    }

    public static class OfflineInsurancePolicy {

        public String warrantyCode;

        public String reason;

        public OfflineInsurancePolicy() {

        }

        public OfflineInsurancePolicy(OfflineInsurancePolicyModel offlineInsurancePolicyModel) {
            this.warrantyCode = offlineInsurancePolicyModel.warranty_code;
            this.reason = offlineInsurancePolicyModel.reason;
        }

    }

    public static class GetInsurancePolicyListByActualPayTimeRequest extends BaseRequest {
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;
        @CheckParams
        public String type;
    }

    public static class GetInsurancePolicyListByActualPayTimeResponse extends BaseResponse {
        public List<GetInsurancePolicyForManagerSystem> data;
    }

    public static class CustWarrantyBrokerage {
        /**
         * 主键
         */
        public String id;

        /**
         * 内部保单唯一标识
         */
        public String warrantyUuid;

        /**
         * 归属账号uuid
         */
        public String managerUuid;

        /**
         * 缴费ID
         */
        public String costId;

        /**
         * 渠道ID
         */
        public String channelId;

        /**
         * 代理人ID
         */
        public String agentId;

        /**
         * 保单佣金
         */
        public String warrantyMoney;
        public String warrantyMoneyText;

        /**
         * 天眼佣金
         */
        public String insMoney;
        public String insMoneyText;

        /**
         * 业管佣金
         */
        public String managerMoney;
        public String managerMoneyText;

        /**
         * 渠道佣金
         */
        public String channelMoney;
        public String channelMoneyText;

        /**
         * 代理人佣金
         */
        public String agentMoney;
        public String agentMoneyText;

        /**
         * 保单佣金比例
         */
        public String warrantyRate;
        public String warrantyRateText;

        /**
         * 天眼佣金比例
         */
        public String insRate;
        public String insRateText;

        /**
         * 业管佣金比例
         */
        public String managerRate;
        public String managerRateText;

        /**
         * 渠道佣金比例
         */
        public String channelRate;
        public String channelRateText;

        /**
         * 代理人佣金比例
         */
        public String agentRate;
        public String agentRateText;

        /**
         * 创建时间
         */
        public String createdAt;
        public String createdAtText;

        /**
         * 结束时间
         */
        public String updatedAt;
        public String updatedAtText;

        public CustWarrantyBrokerage() {

        }

        public CustWarrantyBrokerage(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {

            if (custWarrantyBrokerageModel == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");

            this.id = custWarrantyBrokerageModel.id;
            this.warrantyUuid = custWarrantyBrokerageModel.warranty_uuid;
            this.managerUuid = custWarrantyBrokerageModel.manager_uuid;
            this.costId = custWarrantyBrokerageModel.cost_id;
            this.channelId = custWarrantyBrokerageModel.id;
            this.agentId = custWarrantyBrokerageModel.id;
            this.warrantyMoney = custWarrantyBrokerageModel.warranty_money;
            this.warrantyMoneyText = "¥" + decimalFormat.format(new BigDecimal(custWarrantyBrokerageModel.warranty_money));
            this.insMoney = custWarrantyBrokerageModel.ins_money;
            this.insMoneyText = "¥" + decimalFormat.format(new BigDecimal(custWarrantyBrokerageModel.ins_money));
            this.managerMoney = custWarrantyBrokerageModel.manager_money;
            this.managerMoneyText = "¥" + decimalFormat.format(new BigDecimal(custWarrantyBrokerageModel.manager_money));
            this.channelMoney = custWarrantyBrokerageModel.channel_money;
            this.channelMoneyText = "¥" + decimalFormat.format(new BigDecimal(custWarrantyBrokerageModel.channel_money));
            this.agentMoney = custWarrantyBrokerageModel.agent_money;
            this.agentMoneyText ="¥" +  decimalFormat.format(new BigDecimal(custWarrantyBrokerageModel.agent_money));
            this.warrantyRate = custWarrantyBrokerageModel.warranty_rate;
            if (StringKit.isNumeric(custWarrantyBrokerageModel.warranty_rate)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyBrokerageModel.warranty_rate);
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
                this.warrantyRateText = decimalFormat.format(multiply.doubleValue()) + "%";
            }
            this.insRate = custWarrantyBrokerageModel.ins_rate;
            if (StringKit.isNumeric(custWarrantyBrokerageModel.ins_rate)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyBrokerageModel.ins_rate);
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
                this.insRateText = decimalFormat.format(multiply.doubleValue()) + "%";
            }
            this.managerRate = custWarrantyBrokerageModel.manager_rate;
            if (StringKit.isNumeric(custWarrantyBrokerageModel.manager_rate)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyBrokerageModel.manager_rate);
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
                this.managerRateText = decimalFormat.format(multiply.doubleValue()) + "%";
            }
            this.channelRate = custWarrantyBrokerageModel.channel_rate;
            if (StringKit.isNumeric(custWarrantyBrokerageModel.channel_rate)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyBrokerageModel.channel_rate);
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
                this.channelRateText = decimalFormat.format(multiply.doubleValue()) + "%";
            }
            this.agentRate = custWarrantyBrokerageModel.agent_rate;
            if (StringKit.isNumeric(custWarrantyBrokerageModel.agent_rate)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyBrokerageModel.agent_rate);
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
                this.agentRateText = decimalFormat.format(multiply.doubleValue()) + "%";
            }

            this.createdAt = custWarrantyBrokerageModel.created_at;
            if (StringKit.isInteger(custWarrantyBrokerageModel.created_at)) {
                this.createdAtText = sdf.format(new Date(Long.valueOf(custWarrantyBrokerageModel.created_at)));
            }
            this.updatedAt = custWarrantyBrokerageModel.updated_at;
            if (StringKit.isInteger(custWarrantyBrokerageModel.updated_at)) {
                this.updatedAtText = sdf.format(new Date(Long.valueOf(custWarrantyBrokerageModel.updated_at)));
            }
        }
    }

}
