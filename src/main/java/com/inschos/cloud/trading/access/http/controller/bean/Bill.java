package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.model.BillDetailModel;
import com.inschos.cloud.trading.model.BillModel;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.OfflineInsurancePolicyModel;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.inschos.cloud.trading.assist.kit.ExcelModelKit.TYPE_DOUBLE;

/**
 * 创建日期：2018/6/25 on 14:17
 * 描述：
 * 作者：zhangyunhe
 */
public class Bill {

    public static class CreateBillRequest extends BaseRequest {
        @CheckParams(hintName = "结算单标识")
        public String billName;
        @CheckParams(hintName = "保险公司")
        public String insuranceCompanyId;
        public String principal;
        public List<BillInsurancePolicy> warrantyList;
        public String remark;
    }

    public static class CreateBillResponse extends BaseResponse {
        public String data;
    }

    public static class AddBillDetailRequest extends BaseRequest {
        @CheckParams(hintName = "结算单唯一标识")
        public String billUuid;
        @CheckParams(hintName = "结算单明细列表")
        public List<BillInsurancePolicy> warrantyList;
    }

    public static class AddBillDetailResponse extends BaseResponse {

    }

    public static class GetBillEnableInsurancePolicyListRequest extends BaseRequest {
        @CheckParams(hintName = "结算单唯一标识")
        public String billUuid;
        // 1-网销，2-线下单
        @CheckParams(hintName = "保单类型")
        public String type;
        // 1-保单号，3-被保险人
        public String searchType;
        public String searchKey;
        // 1-签单时间（下单时间），2-起保时间，3-缴费时间
        public String timeType;
        public String startTime;
        public String endTime;
    }

    public static class GetBillEnableInsurancePolicyListResponse extends BaseResponse {
        public List<BillInsurancePolicy> data;
    }

    public static class GetBillListRequest extends BaseRequest {
        // 1-保险公司，2-负责人
        public String searchType;
        public String searchKey;
        // 结算状态，0-未结算，1-已结算
        public String billStatus;
    }

    public static class GetBillListResponse extends BaseResponse {
        public List<BillBean> data;
    }

    public static class GetBillInfoRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
    }

    public static class GetBillInfoResponse extends BaseResponse {
        public BillBean data;
    }

    public static class GetBillDetailRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
        // 1-保单号，2-保险产品，3-被保险人
        public String searchType;
        public String searchKey;
    }

    public static class GetBillDetailResponse extends BaseResponse {
        public List<BillInsurancePolicy> data;
    }

    public static class DownloadBillDetailRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
    }

    public static class DownloadBillDetailResponse extends BaseResponse {
        public String data;
    }

    public static class ClearingBillRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
    }

    public static class ClearingBillResponse extends BaseResponse {

    }

    public static class CancelClearingBillRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
    }

    public static class CancelClearingBillResponse extends BaseResponse {

    }

    public static class DeleteBillRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
    }

    public static class DeleteBillResponse extends BaseResponse {

    }

    public static class DeleteBillDetailRequest extends BaseRequest {
        @CheckParams(hintName = "结算单uuid")
        public String billUuid;
        @CheckParams(hintName = "结算单明细")
        public List<BillInsurancePolicy> billDetails;
    }

    public static class DeleteBillDetailResponse extends BaseResponse {

    }

    public static class BillBean {

        //主键id
        public String id;

        //业管uuid
        public String managerUuid;

        //结算单号
        public String billUuid;

        //结算标识
        public String billName;

        //保险公司
        public String insuranceCompanyId;
        public String insuranceCompanyName;

        //负责人
        public String principal;
        public String principalName;

        //结算状态，0-未结算，1-结算
        public String isSettlement;
        public String isSettlementText;

        //结算金额
        public String billMoney;
        public String billMoneyText;

        //结算时间
        public String billTime;
        public String billTimeText;

        //备注
        public String remark;

        //创建时间
        public String createdAt;
        public String createdAtText;

        //更新时间
        public String updatedAt;
        public String updatedAtText;

        public BillBean() {

        }

        public BillBean(BillModel model) {
            if (model == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            this.id = model.id;
            this.managerUuid = model.manager_uuid;
            this.billUuid = model.bill_uuid;
            this.billName = model.bill_name;
            this.insuranceCompanyId = model.insurance_company_id;
            this.insuranceCompanyName = model.insurance_company_name;
            this.principal = model.principal;
            this.isSettlement = model.is_settlement;
            if (!StringKit.isEmpty(model.is_settlement)) {
                this.isSettlementText = model.isSettlementText(model.is_settlement);
            }
            this.billMoney = model.bill_money;
            if (StringKit.isNumeric(model.bill_money)) {
                BigDecimal bigDecimal = new BigDecimal(model.bill_money);
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                this.billMoney = decimalFormat.format(bigDecimal.doubleValue());
            } else {
                this.billMoney = "0.00";
            }
            this.billMoneyText = "¥" + this.billMoney;
            this.billTime = model.bill_time;
            if (StringKit.isInteger(model.bill_time)) {
                this.billTimeText = sdf.format(new Date(Long.valueOf(this.billTime)));
            }
            this.remark = model.remark;
            this.createdAt = model.created_at;
            if (StringKit.isInteger(model.created_at)) {
                this.createdAtText = sdf.format(new Date(Long.valueOf(this.createdAt)));
            }
            this.updatedAt = model.updated_at;
            if (StringKit.isInteger(model.updated_at)) {
                this.updatedAtText = sdf.format(new Date(Long.valueOf(this.updatedAt)));
            }
        }

    }

    public static final List<String> BILL_DETAIL_LIST;

    static {
        BILL_DETAIL_LIST = new ArrayList<>();

        BILL_DETAIL_LIST.add("warrantyCode");
        BILL_DETAIL_LIST.add("productName");
        BILL_DETAIL_LIST.add("insuredName");
        BILL_DETAIL_LIST.add("premium");
        BILL_DETAIL_LIST.add("phase");
        BILL_DETAIL_LIST.add("feeRatePercentage");
        BILL_DETAIL_LIST.add("fee");
        BILL_DETAIL_LIST.add("startTimeText");
        BILL_DETAIL_LIST.add("endTimeText");
        BILL_DETAIL_LIST.add("orderTimeText");
        BILL_DETAIL_LIST.add("billTimeText");

    }

    public static final Map<String, String> BILL_DETAIL_FIELD_TYPE;

    static {
        BILL_DETAIL_FIELD_TYPE = new HashMap<>();
        BILL_DETAIL_FIELD_TYPE.put("premium", TYPE_DOUBLE);
        BILL_DETAIL_FIELD_TYPE.put("feeRatePercentage", TYPE_DOUBLE);
        BILL_DETAIL_FIELD_TYPE.put("fee", TYPE_DOUBLE);
    }

    public static BillInsurancePolicy getDownloadBillDetailTitle() {
        BillInsurancePolicy billInsurancePolicy = new BillInsurancePolicy();

        billInsurancePolicy.warrantyCode = "保单号";
        billInsurancePolicy.productName = "保险产品";
        billInsurancePolicy.insuredName = "被保险人";
        billInsurancePolicy.premium = "保费（元）";
        billInsurancePolicy.phase = "缴费期";
        billInsurancePolicy.feeRatePercentage = "佣金比（%）";
        billInsurancePolicy.fee = "佣金（元）";
        billInsurancePolicy.startTimeText = "起保时间";
        billInsurancePolicy.endTimeText = "终止时间";
        billInsurancePolicy.orderTimeText = "签单时间";
        billInsurancePolicy.billTimeText = "结算时间";

        return billInsurancePolicy;
    }

    public static class BillInsurancePolicy {

        public String id;

        // 保单号
        public String warrantyCode;

        // 保单uuid
        public String warrantyUuid;

        // 支付id
        public String costId;

        // 保险公司名
        public String companyId;
        public String companyName;

        // 保险产品
        public String productId;
        public String productName;

        // 被保险人
        public String insuredName;

        // 保费(元)
        public String premium;
        public String premiumText;

        // 起保时间
        public String startTime;
        public String startTimeText;

        // 终止时间
        public String endTime;
        public String endTimeText;

        // 结算时间
        public String billTime;
        public String billTimeText;

        // 签单时间
        public String orderTime;
        public String orderTimeText;

        // 生成时间
        public String createdAt;
        public String createdAtText;

        // 缴费期
        public String phase;

        // 手续费（%）
        public String feeRate;
        public String feeRatePercentage;
        public String feeRateText;

        // 手续费（元）
        public String fee;
        public String feeText;

        // 保单类型，1-网销，2-线下单
        public String warrantyType;
        public String warrantyTypeText;

        public BillInsurancePolicy() {

        }

        public BillInsurancePolicy(CustWarrantyCostModel custWarrantyCostModel) {
            if (custWarrantyCostModel == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");

            this.id = custWarrantyCostModel.id;
            this.warrantyCode = custWarrantyCostModel.warranty_code;
            this.warrantyUuid = custWarrantyCostModel.warranty_uuid;
            this.costId = custWarrantyCostModel.id;
            this.companyId = custWarrantyCostModel.ins_company_id;
            this.productId = custWarrantyCostModel.product_id;
            this.insuredName = custWarrantyCostModel.person_name;
            if (StringKit.isNumeric(custWarrantyCostModel.premium)) {
                this.premium = decimalFormat.format(new BigDecimal(custWarrantyCostModel.premium).doubleValue());
            } else {
                this.premium = "0.00";
            }
            this.premiumText = "¥" + this.premium;
            this.startTime = custWarrantyCostModel.warranty_start_time;
            if (StringKit.isInteger(custWarrantyCostModel.warranty_start_time)) {
                this.startTimeText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.warranty_start_time)));
            }
            this.endTime = custWarrantyCostModel.warranty_end_time;
            if (StringKit.isInteger(custWarrantyCostModel.warranty_end_time)) {
                this.endTimeText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.warranty_end_time)));
            }
            this.orderTime = custWarrantyCostModel.created_at;
            if (StringKit.isInteger(custWarrantyCostModel.created_at)) {
                this.orderTimeText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.created_at)));
            }
            this.createdAt = custWarrantyCostModel.created_at;
            if (StringKit.isInteger(custWarrantyCostModel.created_at)) {
                this.createdAtText = sdf.format(new Date(Long.valueOf(custWarrantyCostModel.created_at)));
            }
            this.phase = custWarrantyCostModel.phase;
            this.feeRate = custWarrantyCostModel.brokerage_manager_rate;
            if (StringKit.isNumeric(custWarrantyCostModel.brokerage_manager_rate)) {
                BigDecimal bigDecimal = new BigDecimal(custWarrantyCostModel.brokerage_manager_rate);
                this.feeRate = decimalFormat.format(bigDecimal.doubleValue());
                bigDecimal = bigDecimal.multiply(new BigDecimal(100));
                this.feeRateText = decimalFormat.format(bigDecimal) + "%";
            } else {
                this.feeRate = "0.00";
                this.feeRateText = "0.00%";
            }
            this.fee = custWarrantyCostModel.brokerage_manager_money;
            if (StringKit.isNumeric(custWarrantyCostModel.brokerage_manager_money)) {
                this.fee = decimalFormat.format(new BigDecimal(custWarrantyCostModel.brokerage_manager_money).doubleValue());
            } else {
                this.fee = "0.00";
            }
            this.feeText = "¥" + this.fee;

            this.feeRatePercentage = decimalFormat.format(new BigDecimal(this.feeRate).multiply(new BigDecimal(100)).doubleValue());

            this.warrantyType = "1";
            this.warrantyTypeText = new BillDetailModel().typeText(this.warrantyType);
        }

        public BillInsurancePolicy(OfflineInsurancePolicyModel offlineInsurancePolicyModel) {
            if (offlineInsurancePolicyModel == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");

            this.id = offlineInsurancePolicyModel.id;
            this.warrantyCode = offlineInsurancePolicyModel.warranty_code;
            this.warrantyUuid = offlineInsurancePolicyModel.warranty_uuid;
            this.costId = offlineInsurancePolicyModel.id;
            this.companyName = offlineInsurancePolicyModel.insurance_company;
            this.productName = offlineInsurancePolicyModel.insurance_product;
            this.insuredName = offlineInsurancePolicyModel.insured_name;

            this.fee = offlineInsurancePolicyModel.brokerage;
            if (StringKit.isNumeric(offlineInsurancePolicyModel.brokerage)) {
                this.fee = decimalFormat.format(new BigDecimal(offlineInsurancePolicyModel.brokerage).doubleValue());
            } else {
                this.fee = "0.00";
            }
            this.feeText = "¥" + this.fee;

            if (StringKit.isNumeric(offlineInsurancePolicyModel.premium)) {
                BigDecimal bigDecimal = new BigDecimal(offlineInsurancePolicyModel.premium);
                this.premium = decimalFormat.format(bigDecimal.doubleValue());
                if (bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal divide = new BigDecimal(offlineInsurancePolicyModel.brokerage).divide(bigDecimal, 6, BigDecimal.ROUND_HALF_DOWN);
                    this.feeRate = decimalFormat.format(divide.doubleValue());
                    BigDecimal multiply = divide.multiply(new BigDecimal("100"));
                    this.feeRateText = decimalFormat.format(multiply.doubleValue()) + "%";
                } else {
                    this.feeRate = "0";
                    this.feeRateText = "0.00%";
                }
            } else {
                this.premium = "0.00";
                this.feeRate = "0";
                this.feeRateText = "0.00%";
            }
            this.premiumText = "¥" + this.premium;

            this.feeRatePercentage = decimalFormat.format(new BigDecimal(this.feeRate).multiply(new BigDecimal(100)).doubleValue());

            this.startTime = offlineInsurancePolicyModel.start_time;
            if (StringKit.isInteger(offlineInsurancePolicyModel.start_time)) {
                this.startTimeText = sdf.format(new Date(Long.valueOf(offlineInsurancePolicyModel.start_time)));
            }
            this.endTime = offlineInsurancePolicyModel.end_time;
            if (StringKit.isInteger(offlineInsurancePolicyModel.end_time)) {
                this.endTimeText = sdf.format(new Date(Long.valueOf(offlineInsurancePolicyModel.end_time)));
            }
            this.orderTime = offlineInsurancePolicyModel.order_time;
            if (StringKit.isInteger(offlineInsurancePolicyModel.order_time)) {
                this.orderTimeText = sdf.format(new Date(Long.valueOf(offlineInsurancePolicyModel.order_time)));
            }
            this.createdAt = offlineInsurancePolicyModel.created_at;
            if (StringKit.isInteger(offlineInsurancePolicyModel.created_at)) {
                this.createdAtText = sdf.format(new Date(Long.valueOf(offlineInsurancePolicyModel.created_at)));
            }
            this.phase = offlineInsurancePolicyModel.payment_time;
            this.warrantyType = "2";
            this.warrantyTypeText = new BillDetailModel().typeText(this.warrantyType);
        }

    }
}
