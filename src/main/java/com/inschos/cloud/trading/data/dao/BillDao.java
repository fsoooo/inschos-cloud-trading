package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.BillDetailMapper;
import com.inschos.cloud.trading.data.mapper.BillMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.OfflineCustWarrantyMapper;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.StringKit;
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
    private OfflineCustWarrantyMapper offlineCustWarrantyMapper;

    public int addBill(Bill bill) {
        int i = billMapper.addBill(bill);

        if (i <= 0) {
            rollBack();
            return i;
        }

        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
        custWarrantyCost.bill_uuid = bill.bill_uuid;
        custWarrantyCost.is_settlement = "1";
        custWarrantyCost.updated_at = bill.updated_at;

        OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
        offlineCustWarranty.bill_uuid = bill.bill_uuid;
        offlineCustWarranty.is_settlement = "1";
        offlineCustWarranty.updated_at = bill.updated_at;

        if (bill.billDetailList != null && !bill.billDetailList.isEmpty()) {
            for (BillDetail billDetail : bill.billDetailList) {
                i = billDetailMapper.addBillDetail(billDetail);

                if (i <= 0) {
                    rollBack();
                    return i;
                }

            }

            i = updateCustWarrantySettlementAndBillUuid(bill.billDetailList, custWarrantyCost, offlineCustWarranty);

            if (i <= 0) {
                rollBack();
                return i;
            }
        }

        return i;
    }

    public int addBillDetail(String bill_uuid, List<BillDetail> list) {
        int i = 1;

        if (list != null && !list.isEmpty()) {
            String time = String.valueOf(System.currentTimeMillis());

            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
            custWarrantyCost.bill_uuid = bill_uuid;
            custWarrantyCost.is_settlement = "1";
            custWarrantyCost.updated_at = time;

            OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
            offlineCustWarranty.bill_uuid = bill_uuid;
            offlineCustWarranty.is_settlement = "1";
            offlineCustWarranty.updated_at = time;

            for (BillDetail billDetail : list) {

                i = billDetailMapper.addBillDetail(billDetail);

                if (i <= 0) {
                    rollBack();
                    return i;
                }

            }

            i = updateCustWarrantySettlementAndBillUuid(list, custWarrantyCost, offlineCustWarranty);

            if (i <= 0) {
                rollBack();
                return i;
            }
        }

        return i;
    }

    public int addBillDetailAndBrokerage(String bill_uuid, String brokerage, List<BillDetail> list) {
        String time = String.valueOf(System.currentTimeMillis());
        int i = addBillDetail(bill_uuid, list);

        if (i <= 0) {
            rollBack();
            return i;
        }

        Bill bill = new Bill();
        bill.bill_uuid = bill_uuid;
        bill.bill_time = time;
        bill.updated_at = time;
        bill.bill_money = brokerage;
        bill.is_settlement = "1";
        i = updateBillSettlementAndMoneyAndTimeByBillUuid(bill);

        if (i <= 0) {
            rollBack();
            return i;
        }

        return i;
    }

    public int updateBillSettlementAndMoneyAndTimeByBillUuid(Bill bill) {
        return billMapper.updateBillSettlementAndMoneyAndTimeByBillUuid(bill);
    }

    public int updateBillMoneyByBillUuid(Bill bill) {
        return billMapper.updateBillMoneyByBillUuid(bill);
    }

    public int deleteBill(Bill bill) {
        BillDetail billDetail = new BillDetail();
        billDetail.bill_uuid = bill.bill_uuid;
        billDetail.page = setPage("0", null, "1000");

        int i = 1;
        boolean flag;
        do {
            List<BillDetail> billDetailByBillUuid = billDetailMapper.findBillDetailByBillUuid(billDetail);

            bill.updated_at = String.valueOf(System.currentTimeMillis());
            bill.state = "0";
            i = billMapper.deleteBill(bill);

            if (i <= 0) {
                rollBack();
                return i;
            }

            if (billDetailByBillUuid == null || billDetailByBillUuid.isEmpty()) {
                return i;
            }

            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
            custWarrantyCost.bill_uuid = "";
            custWarrantyCost.is_settlement = "0";
            custWarrantyCost.updated_at = bill.updated_at;

            OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
            offlineCustWarranty.bill_uuid = "";
            offlineCustWarranty.is_settlement = "0";
            offlineCustWarranty.updated_at = bill.updated_at;

            i = updateCustWarrantySettlementAndBillUuid(billDetailByBillUuid, custWarrantyCost, offlineCustWarranty);

            if (i <= 0) {
                rollBack();
                return i;
            }

            flag = billDetailByBillUuid.size() >= 1000;

        } while (flag);

        return i;
    }

    public int deleteBillDetailByIds(List<BillDetail> ids, String billUuid, BigDecimal bigDecimal) {

        String time = String.valueOf(System.currentTimeMillis());
        Bill billByBillUuid = billMapper.findBillByBillUuid(billUuid);

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

        Bill bill = new Bill();
        bill.bill_uuid = billUuid;
        bill.bill_money = decimalFormat.format(billMoney.doubleValue());
        bill.updated_at = time;

        int i = billMapper.updateBillMoneyByBillUuid(bill);

        if (i <= 0) {
            rollBack();
            return i;
        }

        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
        custWarrantyCost.bill_uuid = "";
        custWarrantyCost.is_settlement = "0";
        custWarrantyCost.updated_at = time;

        OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
        offlineCustWarranty.bill_uuid = "";
        offlineCustWarranty.is_settlement = "0";
        offlineCustWarranty.updated_at = time;

        return updateCustWarrantySettlementAndBillUuid(ids, custWarrantyCost, offlineCustWarranty);
    }

    public int clearingBill(Bill bill) {
        bill.is_settlement = "1";
        bill.updated_at = String.valueOf(System.currentTimeMillis());
        int i = billMapper.updateSettlementByBillUuid(bill);

        if (i <= 0) {
            rollBack();
            return i;
        }

        BillDetail billDetail = new BillDetail();
        billDetail.bill_uuid = bill.bill_uuid;
        billDetail.page = setPage("0", null, "1000");

        boolean flag;
        do {
            List<BillDetail> billDetailByBillUuid = billDetailMapper.findBillDetailByBillUuid(billDetail);

            if (billDetailByBillUuid != null && !billDetailByBillUuid.isEmpty()) {
                CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
                custWarrantyCost.bill_uuid = bill.bill_uuid;
                custWarrantyCost.is_settlement = "1";
                custWarrantyCost.updated_at = bill.updated_at;

                OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
                offlineCustWarranty.bill_uuid = bill.bill_uuid;
                offlineCustWarranty.is_settlement = "1";
                offlineCustWarranty.updated_at = bill.updated_at;

                i = updateCustWarrantySettlementAndBillUuid(billDetailByBillUuid, custWarrantyCost, offlineCustWarranty);

                if (i <= 0) {
                    rollBack();
                    return i;
                }
            }

            flag = billDetailByBillUuid != null && billDetailByBillUuid.size() >= 1000;

        } while (flag);

        return i;
    }

    public int cancelClearingBill(Bill bill) {
        bill.is_settlement = "0";
        bill.updated_at = String.valueOf(System.currentTimeMillis());
        int i = billMapper.updateSettlementByBillUuid(bill);

        if (i <= 0) {
            rollBack();
            return i;
        }

        BillDetail billDetail = new BillDetail();
        billDetail.bill_uuid = bill.bill_uuid;
        billDetail.page = setPage("0", null, "1000");

        boolean flag;
        do {
            List<BillDetail> billDetailByBillUuid = billDetailMapper.findBillDetailByBillUuid(billDetail);

            if (billDetailByBillUuid != null && !billDetailByBillUuid.isEmpty()) {
                CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
                custWarrantyCost.bill_uuid = bill.bill_uuid;
                custWarrantyCost.is_settlement = "0";
                custWarrantyCost.updated_at = bill.updated_at;

                OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
                offlineCustWarranty.bill_uuid = bill.bill_uuid;
                offlineCustWarranty.is_settlement = "0";
                offlineCustWarranty.updated_at = bill.updated_at;

                i = updateCustWarrantySettlementAndBillUuid(billDetailByBillUuid, custWarrantyCost, offlineCustWarranty);

                if (i <= 0) {
                    rollBack();
                    return i;
                }
            }

            flag = billDetailByBillUuid != null && billDetailByBillUuid.size() >= 1000;
        } while (flag);

        return i;
    }

    public int updateSettlementByBillUuid(Bill bill) {
        return billMapper.updateSettlementByBillUuid(bill);
    }

    private int updateCustWarrantySettlementAndBillUuid(List<BillDetail> list, CustWarrantyCost custWarrantyCost, OfflineCustWarranty offlineCustWarranty) {
        int i = 1;

        if (list != null && !list.isEmpty()) {
            for (BillDetail billDetail : list) {

                if (StringKit.equals(billDetail.type, BillDetail.TYPE_ON_LINE)) {

                    custWarrantyCost.id = billDetail.cost_id;

                    if (StringKit.equals(custWarrantyCost.is_settlement,"0")) {
                        i = billDetailMapper.deleteBillDetailById(billDetail.id);

                        if (i <= 0) {
                            rollBack();
                            return i;
                        }
                    }


                    i = custWarrantyCostMapper.updateSettlementAndBillUuidByCostId(custWarrantyCost);
                } else if (StringKit.equals(billDetail.type, BillDetail.TYPE_OFF_LINE)) {

                    offlineCustWarranty.warranty_uuid = billDetail.warranty_uuid;

                    if (StringKit.equals(offlineCustWarranty.is_settlement,"0")) {
                        i = billDetailMapper.deleteBillDetailById(billDetail.id);

                        if (i <= 0) {
                            rollBack();
                            return i;
                        }
                    }

                    i = offlineCustWarrantyMapper.updateSettlementAndBillUuidByWarrantyUuid(offlineCustWarranty);
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

    public Bill findBillByBillUuid(String bill_uuid) {
        return billMapper.findBillByBillUuid(bill_uuid);
    }

    public Bill findBillByBillName(Bill bill){
        return billMapper.findBillByBillName(bill);
    }

    public List<Bill> findBillByManagerUuid(Bill bill) {
        return billMapper.findBillByManagerUuid(bill);
    }

    public long findBillCountByManagerUuid(Bill bill) {
        return billMapper.findBillCountByManagerUuid(bill);
    }

    public List<Bill> findBillByInsuranceCompany(Bill bill) {
        return billMapper.findBillByInsuranceCompany(bill);
    }

    protected Page setPage(String lastId, String num, String size) {
        Page page = new Page();

        if (StringKit.isInteger(size)) {
            if (StringKit.isInteger(lastId)) {
                page.lastId = Long.valueOf(lastId);
                page.offset = Integer.valueOf(size);
            } else if (StringKit.isInteger(num)) {
                int pageSize = Integer.valueOf(size);
                int pageStart = (Integer.valueOf(num) - 1) * pageSize;

                page.start = pageStart;
                page.offset = pageSize;
            }
        }
        return page;
    }
}
