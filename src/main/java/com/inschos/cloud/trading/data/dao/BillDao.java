package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.access.http.controller.bean.Bill;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.BillDetailMapper;
import com.inschos.cloud.trading.data.mapper.BillMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.OfflineInsurancePolicyMapper;
import com.inschos.cloud.trading.model.BillDetailModel;
import com.inschos.cloud.trading.model.BillModel;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.OfflineInsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 创建日期：2018/6/25 on 14:20
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class BillDao extends BaseDao {

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private BillDetailMapper billDetailMapper;

    @Autowired
    private CustWarrantyCostMapper custWarrantyCostMapper;

    @Autowired
    private OfflineInsurancePolicyMapper offlineInsurancePolicyMapper;

    public int addBill(BillModel billModel) {

        int i = billMapper.addBill(billModel);

        if (i <= 0) {
            rollBack();
            return i;
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.bill_uuid = billModel.bill_uuid;
        custWarrantyCostModel.updated_at = billModel.updated_at;

        OfflineInsurancePolicyModel offlineInsurancePolicyModel = new OfflineInsurancePolicyModel();
        offlineInsurancePolicyModel.bill_uuid = billModel.bill_uuid;
        offlineInsurancePolicyModel.updated_at = billModel.updated_at;

        if (billModel.billDetailModelList != null && !billModel.billDetailModelList.isEmpty()) {
            for (BillDetailModel billDetailModel : billModel.billDetailModelList) {
                i = billDetailMapper.addBillDetail(billDetailModel);

                if (i <= 0) {
                    rollBack();
                    return i;
                }

                if (StringKit.equals(billDetailModel.type, BillDetailModel.TYPE_ON_LINE)) {
                    custWarrantyCostModel.id = billDetailModel.cost_id;
                    i = custWarrantyCostMapper.updateBillUuidByCostId(custWarrantyCostModel);
                } else if (StringKit.equals(billDetailModel.type, BillDetailModel.TYPE_OFF_LINE)) {
                    offlineInsurancePolicyModel.warranty_uuid = billDetailModel.warranty_uuid;
                    i = offlineInsurancePolicyMapper.updateBillUuidByWarrantyUuid(offlineInsurancePolicyModel);
                } else {
                    i = -1;
                }

                if (i <= 0) {
                    rollBack();
                    return i;
                }
            }
        }

        return i;
    }

    public int updateBillSettlementAndMoneyAndTimeByBillUuid(BillModel billModel) {
        return billMapper.updateBillSettlementAndMoneyAndTimeByBillUuid(billModel);
    }

    public int updateBillMoneyByBillUuid(BillModel billModel) {
        return billMapper.updateBillMoneyByBillUuid(billModel);
    }

    public int deleteBill(BillModel billModel) {
        List<BillDetailModel> billDetailByBillUuid = billDetailMapper.findBillDetailByBillUuid(billModel.bill_uuid);

        billModel.updated_at = String.valueOf(System.currentTimeMillis());
        billModel.state = "0";
        int i = billMapper.deleteBill(billModel);

        if (i <= 0) {
            rollBack();
            return i;
        }

        if (billDetailByBillUuid == null || billDetailByBillUuid.isEmpty()) {
            return i;
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.bill_uuid = "";
        custWarrantyCostModel.is_settlement = "0";
        custWarrantyCostModel.updated_at = billModel.updated_at;

        OfflineInsurancePolicyModel offlineInsurancePolicyModel = new OfflineInsurancePolicyModel();
        offlineInsurancePolicyModel.bill_uuid = "";
        offlineInsurancePolicyModel.is_settlement = "0";
        offlineInsurancePolicyModel.updated_at = billModel.updated_at;

        return updateCustWarrantySettlementAndBillUuid(billDetailByBillUuid, custWarrantyCostModel, offlineInsurancePolicyModel);
    }

    public int deleteBillDetailByIds(List<BillDetailModel> ids, String billUuid, BigDecimal bigDecimal) {

        String time = String.valueOf(System.currentTimeMillis());
        BillModel billByBillUuid = billMapper.findBillByBillUuid(billUuid);

        if (billByBillUuid == null) {
            return -1;
        }

        BigDecimal billMoney;
        if (StringKit.isNumeric(billByBillUuid.bill_money)) {
            billMoney = new BigDecimal(billByBillUuid.bill_money);
        } else {
            billMoney = new BigDecimal(0);
        }

        billMoney = billMoney.subtract(bigDecimal);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        BillModel billModel = new BillModel();
        billModel.bill_uuid = billUuid;
        billModel.bill_money = decimalFormat.format(billMoney.doubleValue());
        billModel.updated_at = time;

        int i = billMapper.updateBillMoneyByBillUuid(billModel);

        if (i <= 0) {
            rollBack();
            return i;
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.bill_uuid = "";
        custWarrantyCostModel.is_settlement = "0";
        custWarrantyCostModel.updated_at = time;

        OfflineInsurancePolicyModel offlineInsurancePolicyModel = new OfflineInsurancePolicyModel();
        offlineInsurancePolicyModel.bill_uuid = "";
        offlineInsurancePolicyModel.is_settlement = "0";
        offlineInsurancePolicyModel.updated_at = time;

        return updateCustWarrantySettlementAndBillUuid(ids, custWarrantyCostModel, offlineInsurancePolicyModel);
    }

    public int clearingBill(BillModel billModel) {
        billModel.is_settlement = "1";
        billModel.updated_at = String.valueOf(System.currentTimeMillis());
        int i = billMapper.updateSettlementByBillUuid(billModel);

        if (i <= 0) {
            rollBack();
            return i;
        }

        List<BillDetailModel> billDetailByBillUuid = billDetailMapper.findBillDetailByBillUuid(billModel.bill_uuid);

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.bill_uuid = billModel.bill_uuid;
        custWarrantyCostModel.is_settlement = "1";
        custWarrantyCostModel.updated_at = billModel.updated_at;

        OfflineInsurancePolicyModel offlineInsurancePolicyModel = new OfflineInsurancePolicyModel();
        offlineInsurancePolicyModel.bill_uuid = billModel.bill_uuid;
        offlineInsurancePolicyModel.is_settlement = "1";
        offlineInsurancePolicyModel.updated_at = billModel.updated_at;

        return updateCustWarrantySettlementAndBillUuid(billDetailByBillUuid, custWarrantyCostModel, offlineInsurancePolicyModel);
    }

    public int cancelClearingBill(BillModel billModel) {
        billModel.is_settlement = "0";
        billModel.updated_at = String.valueOf(System.currentTimeMillis());
        int i = billMapper.updateSettlementByBillUuid(billModel);

        if (i <= 0) {
            rollBack();
            return i;
        }

        List<BillDetailModel> billDetailByBillUuid = billDetailMapper.findBillDetailByBillUuid(billModel.bill_uuid);

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.bill_uuid = billModel.bill_uuid;
        custWarrantyCostModel.is_settlement = "0";
        custWarrantyCostModel.updated_at = billModel.updated_at;

        OfflineInsurancePolicyModel offlineInsurancePolicyModel = new OfflineInsurancePolicyModel();
        offlineInsurancePolicyModel.bill_uuid = billModel.bill_uuid;
        offlineInsurancePolicyModel.is_settlement = "0";
        offlineInsurancePolicyModel.updated_at = billModel.updated_at;

        return updateCustWarrantySettlementAndBillUuid(billDetailByBillUuid, custWarrantyCostModel, offlineInsurancePolicyModel);
    }

    public int updateSettlementByBillUuid(BillModel billModel) {
        return billMapper.updateSettlementByBillUuid(billModel);
    }

    private int updateCustWarrantySettlementAndBillUuid(List<BillDetailModel> list, CustWarrantyCostModel custWarrantyCostModel, OfflineInsurancePolicyModel offlineInsurancePolicyModel) {
        int i = 1;

        if (list != null && !list.isEmpty()) {
            for (BillDetailModel billDetail : list) {
                i = billDetailMapper.deleteBillDetailById(billDetail.id);

                if (i <= 0) {
                    rollBack();
                    return i;
                }

                if (StringKit.equals(billDetail.type, BillDetailModel.TYPE_ON_LINE)) {
                    custWarrantyCostModel.id = billDetail.cost_id;
                    i = custWarrantyCostMapper.updateSettlementAndBillUuidByCostId(custWarrantyCostModel);
                } else if (StringKit.equals(billDetail.type, BillDetailModel.TYPE_OFF_LINE)) {
                    offlineInsurancePolicyModel.warranty_uuid = billDetail.warranty_uuid;
                    i = offlineInsurancePolicyMapper.updateSettlementAndBillUuidByWarrantyUuid(offlineInsurancePolicyModel);
                } else {
                    i = -1;
                }

                if (i <= 0) {
                    rollBack();
                    return i;
                }
            }
        }

        return i;
    }

    public BillModel findBillByBillUuid(String bill_uuid) {
        return billMapper.findBillByBillUuid(bill_uuid);
    }

    public List<BillModel> findBillByManagerUuid(BillModel billModel) {
        return billMapper.findBillByManagerUuid(billModel);
    }

    public long findBillCountByManagerUuid(BillModel billModel){
        return billMapper.findBillCountByManagerUuid(billModel);
    }

    public List<BillModel> findBillByInsuranceCompany(BillModel billModel) {
        return billMapper.findBillByInsuranceCompany(billModel);
    }
}
