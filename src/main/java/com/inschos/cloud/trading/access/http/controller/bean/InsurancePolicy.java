package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.access.http.controller.action.CarInsuranceAction;
import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.StringKit;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.inschos.cloud.trading.assist.kit.ExcelModelKit.TYPE_DOUBLE;

/**
 * 创建日期：2018/3/22 on 14:03
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicy {

    public static class GetInsurancePolicyListRequest extends BaseRequest {
        // 保单状态，1-待支付 2-待生效 3-保障中 4-已失效
        public String warrantyStatus;
        // 搜索关键字
        public String searchKey;
        // 搜索关键字类型，1-保单号 2-代理人 3-投保人 4-车牌号 5-保险公司 6-保险产品 7被保人
        public String searchType;
        // 时间类型，1-签单时间（下单时间），2-起保时间，3-缴费时间
        public String timeType;
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;
        // 保单类型
        public String warrantyType;
        // 保险产品(关键字)
        public String insuranceProductKey;
        // 保险公司(关键字)
        public String insuranceCompanyKey;
        // 查询方式： 默认账号, 1：代理人业绩
        public String queryWay;


    }

    public static class GetInsurancePolicyListResponse extends BaseResponse {
        public List<GetInsurancePolicyItemBean> data;
    }

    public static class GetInsurancePolicyDetailRequest extends BaseRequest {
        // 保单唯一id
        @CheckParams(hintName = "保单唯一标识")
        public String warrantyUuid;
    }

    public static class GetInsurancePolicyDetailResponse extends BaseResponse {
        public GetInsurancePolicyDetail data;
    }

    public static class DownloadInsurancePolicyListForManagerSystemResponse extends BaseResponse {
        public String data;
    }

    public static class GetDownInsurancePolicyCountForManagerSystem extends BaseResponse {
        public DownInsurancePolicy data;
    }

    public static class OfflineInsurancePolicyInputRequest extends BaseRequest {
        @CheckParams(hintName = "文件")
        public String fileKey;
    }

    public static class OfflineInsurancePolicyInputResponse extends BaseResponse {
        public OfflineInsurancePolicyDetail data;
    }

    public static class GetOfflineInsurancePolicyInputTemplateRequest extends BaseRequest {

    }

    public static class GetOfflineInsurancePolicyInputTemplateResponse extends BaseResponse {
        public String fileUrl;
    }

    public static class GetOfflineInsurancePolicyListRequest extends BaseRequest {
        public String companyName;
        public String channelName;
        public String productName;
        public String timeType;
        public String startTime;
        public String endTime;
    }

    public static class GetOfflineInsurancePolicyListResponse extends BaseResponse {
        public List<OfflineInsurancePolicy> data;
    }

    public static class GetOfflineInsurancePolicyDetailRequest extends BaseRequest {
        @CheckParams(hintName = "保单唯一标识")
        public String warrantyUuid;
    }

    public static class GetOfflineInsurancePolicyDetailResponse extends BaseResponse {
        public OfflineInsurancePolicy data;
    }

    public static class GetInsurancePolicyListByActualPayTimeRequest extends BaseRequest {
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;
        @CheckParams(hintName = "查询类型")
        public String type;
    }

    public static class GetInsurancePolicyListByActualPayTimeResponse extends BaseResponse {
        public List<GetInsurancePolicyItemBean> data;
    }

    public static class UpdateOfflineInsurancePolicyPayStatusRequest extends BaseRequest {
        @CheckParams(hintName = "保单uuid")
        public String warrantyUuid;
        @CheckParams(hintName = "支付状态")
        public String payStatus;
    }

    public static class UpdateOfflineInsurancePolicyPayStatusResponse extends BaseResponse {

    }

    public static class DeleteOfflineInsurancePolicyRequest extends BaseRequest {
        @CheckParams(hintName = "保单uuid")
        public String warrantyUuid;
    }

    public static class DeleteOfflineInsurancePolicyResponse extends BaseResponse {

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
        public String channelName;

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
        public String payCategoryId;
        public String payCategoryName;

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
        public String payStatusText;

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

        //缴费时间
        public String actualPayTime;

        //缴费时间（显示用）
        public String actualPayTimeText;

        // 被保险人
        public String policyholderText;
        public String insuredText;
        public String insuredDetailText;

        public String insuranceProductName;

        public String insuranceCompanyName;
        public String insuranceCompanyLogo;

        // 车险用验证码（仅车险存在）
        public String bjCodeFlag;
        // 车险流水号
        public String bizId;
        public String insuranceClaimsCount = "0";
        public String groupName;

        public String warrantyMoney;
        public String warrantyMoneyText;
        public String insMoney;
        public String insMoneyText;
        public String managerMoney;
        public String managerMoneyText;
        public String channelMoney;
        public String channelMoneyText;
        public String agentMoney;
        public String agentMoneyText;

        public List<CustWarrantyBrokerage> brokerageList;

        public GetInsurancePolicy() {
        }

        public GetInsurancePolicy(InsurancePolicyModel model) {
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
            this.payCategoryId = model.pay_category_id;
//            this.isSettlement = model.is_settlement;
//            this.isSettlementText = model.isSettlementText(isSettlement);
            this.warrantyUrl = model.warranty_url;
            this.warrantyFrom = model.warranty_from;
            this.warrantyFromText = model.warrantyFromText(warrantyFrom);
            this.type = model.type;
            this.integral = model.integral;
            this.payStatus = model.pay_status;
            this.warrantyStatus = model.warranty_status;
            CustWarrantyCostModel model1 = new CustWarrantyCostModel();
            this.payStatusText = model1.payStatusText(payStatus);

            if (StringKit.equals(this.warrantyStatus, InsurancePolicyModel.POLICY_STATUS_PENDING)) {
                this.warrantyStatusText = payStatusText;
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
            this.actualPayTime = model.actual_pay_time;
            if (StringKit.isInteger(model.actual_pay_time)) {
                this.actualPayTimeText = sdf.format(new Date(Long.valueOf(model.actual_pay_time)));
            }

            this.expressEmail = model.express_email;
            this.expressAddress = model.express_address;
            this.expressProvinceCode = model.express_province_code;
            this.expressCityCode = model.express_city_code;
            this.expressCountyCode = model.express_county_code;

            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            if (StringKit.isNumeric(model.warranty_money)) {
                this.warrantyMoney = decimalFormat.format(new BigDecimal(model.warranty_money).doubleValue());
            } else {
                this.warrantyMoney = "0.00";
            }
            this.warrantyMoneyText = "¥" + this.warrantyMoney;
            if (StringKit.isNumeric(model.ins_money)) {
                this.insMoney = decimalFormat.format(new BigDecimal(model.ins_money).doubleValue());
            } else {
                this.insMoney = "0.00";
            }
            this.insMoneyText = "¥" + this.insMoney;
            if (StringKit.isNumeric(model.manager_money)) {
                this.managerMoney = decimalFormat.format(new BigDecimal(model.manager_money).doubleValue());
            } else {
                this.managerMoney = "0.00";
            }
            this.managerMoneyText = "¥" + this.warrantyMoney;
            if (StringKit.isNumeric(model.channel_money)) {
                this.channelMoney = decimalFormat.format(new BigDecimal(model.channel_money).doubleValue());
            } else {
                this.channelMoney = "0.00";
            }
            this.channelMoneyText = "¥" + this.channelMoney;
            if (StringKit.isNumeric(model.agent_money)) {
                this.agentMoney = decimalFormat.format(new BigDecimal(model.agent_money).doubleValue());
            } else {
                this.agentMoney = "0.00";
            }
            this.agentMoneyText = "¥" + this.agentMoney;

            if (StringKit.isNumeric(model.premium)) {
                this.premium = decimalFormat.format(new BigDecimal(model.premium).doubleValue());
            } else {
                this.premium = "0.00";
            }
            this.premiumText = "¥" + this.premium;
            if (StringKit.isNumeric(model.pay_money)) {
                this.payMoney = decimalFormat.format(new BigDecimal(model.pay_money).doubleValue());
            } else {
                this.payMoney = "0.00";
            }
            this.payMoneyText = "¥" + this.payMoney;
            if (StringKit.isNumeric(model.tax_money)) {
                this.taxMoney = decimalFormat.format(new BigDecimal(model.tax_money).doubleValue());
            } else {
                this.taxMoney = "0.00";
            }
            this.taxMoneyText = "¥" + this.taxMoney;
        }

        public GetInsurancePolicy(InsurancePolicyModel model, BigDecimal premium, BigDecimal pay_money, BigDecimal tax_money, String warrantyStatusForPay, String warrantyStatusForPayText) {
            this(model);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            if (premium != null) {
                this.premium = decimalFormat.format(premium.doubleValue());
            } else {
                this.premium = "0.00";
            }
            this.premiumText = "¥" + this.premium;
            if (payMoney != null) {
                this.payMoney = decimalFormat.format(pay_money.doubleValue());
            } else {
                this.payMoney = "0.00";
            }
            this.payMoneyText = "¥" + this.payMoney;
            if (taxMoney != null) {
                this.taxMoney = decimalFormat.format(tax_money.doubleValue());
            } else {
                this.taxMoney = "0.00";
            }
            this.taxMoneyText = "¥" + this.taxMoney;
            this.payStatus = warrantyStatusForPay;
            if (StringKit.equals(this.warrantyStatus, InsurancePolicyModel.POLICY_STATUS_PENDING)) {
                this.warrantyStatusText = warrantyStatusForPayText;
            } else {
                this.warrantyStatusText = model.warrantyStatusText(warrantyStatus);
            }
        }

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

        public List<CustWarrantyCost> costList;

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

                    o.dealInsuranceInfoSort();
                }

                this.coverageList.sort((o1, o2) -> o1.sort - o2.sort);
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

    public static class GetInsurancePolicyItemBean extends GetInsurancePolicy {
        public String contactsName;
        public String contactsMobile;
        public String policyHolderName;
        public String policyHolderMobile;
        public String frameNo;
        public String carCode;

//        public long costDuring;
//        public long costDealDuring;
//        public long productDuring;
//        public long personDuring;
//        public long carDuring;
//        public long roundDuring;

        public GetInsurancePolicyItemBean() {

        }

        public GetInsurancePolicyItemBean(InsurancePolicyModel model) {
            super(model);

            this.policyHolderName = model.policy_holder_name;
            this.policyHolderMobile = model.policy_holder_phone;
            this.carCode = model.car_code;
        }

        public GetInsurancePolicyItemBean(InsurancePolicyModel model, BigDecimal premium, BigDecimal pay_money, BigDecimal tax_money, String warrantyStatusForPay, String warrantyStatusForPayText) {
            super(model, premium, pay_money, tax_money, warrantyStatusForPay, warrantyStatusForPayText);
        }

        public static GetInsurancePolicyItemBean getCarInsurancePolicyTitle() {
            GetInsurancePolicyItemBean insurancePolicy = new GetInsurancePolicyItemBean();

            insurancePolicy.warrantyCode = "保单号";
            insurancePolicy.insuredDetailText = "被保险人";
            insurancePolicy.policyHolderName = "投保人";
            insurancePolicy.productName = "保险产品";
            insurancePolicy.insuranceCompanyName = "保险公司";
            insurancePolicy.createdAtText = "签单日期";
            insurancePolicy.startTimeText = "起保日期";
            insurancePolicy.premium = "保费（元）";
            insurancePolicy.payStatusText = "保费支付状态";
            insurancePolicy.managerMoney = "应收佣金";
            insurancePolicy.channelName = "归属机构";
            insurancePolicy.agentName = "归属人员";

            return insurancePolicy;
        }
    }

    public static final List<String> CAR_FIELD_LIST;

    static {
        CAR_FIELD_LIST = new ArrayList<>();
        CAR_FIELD_LIST.add("warrantyCode");
        CAR_FIELD_LIST.add("insuredDetailText");
        CAR_FIELD_LIST.add("policyHolderName");
        CAR_FIELD_LIST.add("productName");
        CAR_FIELD_LIST.add("insuranceCompanyName");
        CAR_FIELD_LIST.add("createdAtText");
        CAR_FIELD_LIST.add("startTimeText");
        CAR_FIELD_LIST.add("premium");
        CAR_FIELD_LIST.add("payStatusText");
        CAR_FIELD_LIST.add("managerMoney");
        CAR_FIELD_LIST.add("channelName");
        CAR_FIELD_LIST.add("agentName");
    }

    public static final Map<String, String> CAR_FIELD_TYPE;

    static {
        CAR_FIELD_TYPE = new HashMap<>();
        CAR_FIELD_TYPE.put("premium", TYPE_DOUBLE);
        CAR_FIELD_TYPE.put("insMoney", TYPE_DOUBLE);
    }

    public static final Map<String, String> PERSON_FIELD_MAP;

    static {
        PERSON_FIELD_MAP = new HashMap<>();
    }

    public static final Map<String, String> TEAM_FIELD_MAP;

    static {
        TEAM_FIELD_MAP = new HashMap<>();
    }

    public static class GetInsurancePolicyStatisticDetailForManagerSystemRequest extends BaseRequest {
        // 时间范围类型，1-今日，2-本月，3-本年
        @CheckParams(hintName = "时间范围")
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

    public static class GetInsurancePolicyStatementListRequest extends BaseRequest {
        // 起保开始时间
        public String startTime;
        // 起保结束时间
        public String endTime;

        public String searchKey;

    }

    public static class GetInsurancePolicyStatementListResponse extends BaseResponse {
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
        public String type;

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
//            this.byStagesWay = brokerageStatisticListModel.by_stages_way;

            this.warrantyStatus = brokerageStatisticListModel.warranty_status;
            this.phase = brokerageStatisticListModel.phase;
            this.type = brokerageStatisticListModel.type;

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

    public static class OfflineInsurancePolicyDetail {
        public String excelFileKey;
        public String excelFileUrl;
        public String successCount;
        public String failCount;
        public List<OfflineInsurancePolicy> list;
    }

    public static class OfflineInsurancePolicy {

        // 主键id
        public String id;

        // 业管id
        public String managerUuid;

        // 保单唯一标识
        public String warrantyUuid;

        // 被保险人
        public String insuredName;

        // 投保人
        public String policyHolderName;

        // 保险公司
        public String insuranceCompany;

        // 险种
        public String insuranceType;

        // 保险产品
        public String insuranceProduct;

        // 保单号
        public String warrantyCode;

        // 缴费期
        public String paymentTime;
//        public String paymentTimeText;

        // 签单日期
        public String orderTime;
        public String orderTimeText;

        // 实收日期
        public String realIncomeTime;
        public String realIncomeTimeText;

        // 起保时间
        public String startTime;
        public String startTimeText;

        // 终止时间
        public String endTime;
        public String endTimeText;

        // 保费
        public String premium;
        public String premiumText;

        // 保费支付状态
        public String payStatus;
        public String payStatusText;

        // 应收佣金
        public String brokerage;
        public String brokerageText;

        // 归属机构
        public String channelName;

        // 归属代理
        public String agentName;

        // 创建时间
        public String createdAt;
        public String createdAtText;

        // 结束时间
        public String updatedAt;
        public String updatedAtText;

        // public String reason;
        public String isSettlement;
        public String isSettlementText;

        public String reason;
        public List<OfflineInsurancePolicyModel.ErrorReason> reasonList;

        public OfflineInsurancePolicy() {

        }

        public OfflineInsurancePolicy(OfflineInsurancePolicyModel offlineInsurancePolicyModel) {
            if (offlineInsurancePolicyModel == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat money = new DecimalFormat("###,###,###,###,##0.00");

            this.id = offlineInsurancePolicyModel.id;
            this.managerUuid = offlineInsurancePolicyModel.manager_uuid;
            this.warrantyUuid = offlineInsurancePolicyModel.warranty_uuid;
            this.insuredName = offlineInsurancePolicyModel.insured_name;
            this.policyHolderName = offlineInsurancePolicyModel.policy_holder_name;
            this.insuranceCompany = offlineInsurancePolicyModel.insurance_company;
            this.insuranceType = offlineInsurancePolicyModel.insurance_type;
            this.insuranceProduct = offlineInsurancePolicyModel.insurance_product;
            this.warrantyCode = offlineInsurancePolicyModel.warranty_code;
            this.paymentTime = offlineInsurancePolicyModel.payment_time;
//            if (StringKit.isInteger(this.paymentTime)) {
//                this.paymentTimeText = sdf.format(new Date(Long.valueOf(this.paymentTime)));
//            }
            this.orderTime = offlineInsurancePolicyModel.order_time;
            if (StringKit.isInteger(this.orderTime)) {
                this.orderTimeText = sdf.format(new Date(Long.valueOf(this.orderTime)));
            }
            this.realIncomeTime = offlineInsurancePolicyModel.real_income_time;
            if (StringKit.isInteger(this.realIncomeTime)) {
                this.realIncomeTimeText = sdf.format(new Date(Long.valueOf(this.realIncomeTime)));
            }
            this.startTime = offlineInsurancePolicyModel.start_time;
            if (StringKit.isInteger(this.startTime)) {
                this.startTimeText = sdf.format(new Date(Long.valueOf(this.startTime)));
            }
            this.endTime = offlineInsurancePolicyModel.end_time;
            if (StringKit.isInteger(this.endTime)) {
                this.endTimeText = sdf.format(new Date(Long.valueOf(this.endTime)));
            }
            if (StringKit.isNumeric(offlineInsurancePolicyModel.premium)) {
                BigDecimal bigDecimal = new BigDecimal(offlineInsurancePolicyModel.premium);
                this.premium = money.format(bigDecimal.doubleValue());
            } else {
                this.premium = "0.00";
            }
            this.premiumText = "¥" + this.premium;
            this.payStatus = offlineInsurancePolicyModel.pay_status;
            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            this.payStatusText = custWarrantyCostModel.payStatusText(offlineInsurancePolicyModel.pay_status);
            if (StringKit.isNumeric(offlineInsurancePolicyModel.brokerage)) {
                BigDecimal bigDecimal = new BigDecimal(offlineInsurancePolicyModel.brokerage);
                this.brokerage = money.format(bigDecimal.doubleValue());
            } else {
                this.brokerage = "0.00";
            }
            this.brokerageText = "¥" + this.brokerage;

            this.channelName = offlineInsurancePolicyModel.channel_name;
            this.agentName = offlineInsurancePolicyModel.agent_name;
            this.createdAt = offlineInsurancePolicyModel.created_at;
            if (StringKit.isInteger(this.createdAt)) {
                this.createdAtText = sdf.format(new Date(Long.valueOf(this.createdAt)));
            }
            this.updatedAt = offlineInsurancePolicyModel.updated_at;
            if (StringKit.isInteger(this.updatedAt)) {
                this.updatedAtText = sdf.format(new Date(Long.valueOf(this.updatedAt)));
            }
            this.reasonList = offlineInsurancePolicyModel.reasonList;
            if (this.reasonList != null && !this.reasonList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int size = this.reasonList.size();
                for (int i = 0; i < size; i++) {
                    OfflineInsurancePolicyModel.ErrorReason errorReason = this.reasonList.get(i);
                    sb.append(errorReason.reason);
                    if (i != size - 1) {
                        sb.append("，");
                    }
                }
                this.reason = sb.toString();
            }
            this.isSettlement = offlineInsurancePolicyModel.is_settlement;
            BillModel billModel = new BillModel();
            this.isSettlementText = billModel.isSettlementText(offlineInsurancePolicyModel.is_settlement);
        }

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
            this.agentMoneyText = "¥" + decimalFormat.format(new BigDecimal(custWarrantyBrokerageModel.agent_money));
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

    public static class DownInsurancePolicy {
        public String count;
        public String time;
        public List<InsurancePolicy.GetInsurancePolicyItemBean> list;
    }

    public static class CustWarrantyCost {

        //主键
        public String id;

        //内部保单唯一标识
        public String warrantyUuid;

        //应支付时间
        public String payTime;
        public String payTimeText;

        //第几期
        public String phase;

        //保单价格
        public String premium;
        public String premiumText;

        //税费
        public String taxMoney;
        public String taxMoneyText;

        //实际支付时间
        public String actualPayTime;
        public String actualPayTimeText;

        //支付方式 1 银联 2 支付宝 3 微信 4现金
        public String payWay;
        public String payWayText;

        //付款金额
        public String payMoney;
        public String payMoneyText;

        //支付状态  201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
        public String payStatus;
        public String payStatusText;

        //结算状态，0-未结算，1-已结算
        public String isSettlement;
        public String isSettlementText;

        //结算单uuid
        public String billUuid;

        //创建时间
        public String createdAt;
        public String createdAtText;

        //结束时间
        public String updatedAt;
        public String updatedAtText;

        public CustWarrantyCost() {

        }

        public CustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel) {
            if (custWarrantyCostModel == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");

            this.id = custWarrantyCostModel.id;
            this.warrantyUuid = custWarrantyCostModel.warranty_uuid;
            this.payTime = custWarrantyCostModel.pay_time;
            if (StringKit.isInteger(custWarrantyCostModel.pay_time)) {
                this.payTimeText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.pay_time)));
            }
            this.phase = custWarrantyCostModel.phase;
            if (StringKit.isNumeric(custWarrantyCostModel.premium)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyCostModel.premium);
                this.premium = decimalFormat.format(bigDecimal.doubleValue());
            } else {
                this.premium = "0.00";
            }
            this.premiumText = "¥" + this.premium;
            if (StringKit.isNumeric(custWarrantyCostModel.tax_money)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyCostModel.tax_money);
                this.taxMoney = decimalFormat.format(bigDecimal.doubleValue());
            } else {
                this.taxMoney = "0.00";
            }
            this.taxMoneyText = "¥" + this.taxMoney;
            this.actualPayTime = custWarrantyCostModel.actual_pay_time;
            if (StringKit.isInteger(custWarrantyCostModel.actual_pay_time)) {
                this.actualPayTimeText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.actual_pay_time)));
            }
            this.payWay = custWarrantyCostModel.pay_way;
            this.payWayText = custWarrantyCostModel.payWayText(custWarrantyCostModel.pay_way);
            if (StringKit.isNumeric(custWarrantyCostModel.pay_money)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyCostModel.pay_money);
                this.payMoney = decimalFormat.format(bigDecimal.doubleValue());
            } else {
                this.payMoney = "0.00";
            }
            this.payMoneyText = "¥" + this.taxMoney;
            this.payStatus = custWarrantyCostModel.pay_status;
            this.payStatusText = custWarrantyCostModel.payStatusText(custWarrantyCostModel.pay_status);
            this.isSettlement = custWarrantyCostModel.is_settlement;
            BillModel billModel = new BillModel();
            this.isSettlementText = billModel.isSettlementText(custWarrantyCostModel.is_settlement);
            this.billUuid = custWarrantyCostModel.id;
            this.createdAt = custWarrantyCostModel.created_at;
            if (StringKit.isInteger(custWarrantyCostModel.created_at)) {
                this.createdAtText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.created_at)));
            }
            this.updatedAt = custWarrantyCostModel.updated_at;
            if (StringKit.isInteger(custWarrantyCostModel.updated_at)) {
                this.updatedAtText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.updated_at)));
            }
        }
    }

}
