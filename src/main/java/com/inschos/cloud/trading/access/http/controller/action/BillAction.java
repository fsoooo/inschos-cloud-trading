package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.BillBean;
import com.inschos.cloud.trading.access.rpc.bean.AgentBean;
import com.inschos.cloud.trading.access.rpc.bean.InsuranceCompanyBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.client.AgentClient;
import com.inschos.cloud.trading.access.rpc.client.CompanyClient;
import com.inschos.cloud.trading.access.rpc.client.FileClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.ExcelModel;
import com.inschos.cloud.trading.assist.kit.ExcelModelKit;
import com.inschos.cloud.trading.assist.kit.WarrantyUuidWorker;
import com.inschos.cloud.trading.data.dao.*;
import com.inschos.cloud.trading.extend.file.FileUpload;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.JsonKit;
import com.inschos.common.assist.kit.MD5Kit;
import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 创建日期：2018/6/25 on 13:52
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class BillAction extends BaseAction {

    @Autowired
    private BillDao billDao;

    @Autowired
    private BillDetailDao billDetailDao;

    @Autowired
    private CustWarrantyDao custWarrantyDao;

    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private OfflineCustWarrantyDao offlineCustWarrantyDao;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private CompanyClient companyClient;

    @Autowired
    private FileClient fileClient;

    public String createBill(ActionBean actionBean) {
        BillBean.CreateBillRequest request = JsonKit.json2Bean(actionBean.body, BillBean.CreateBillRequest.class);
        BillBean.CreateBillResponse response = new BillBean.CreateBillResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        String time = String.valueOf(System.currentTimeMillis());

        Bill bill = new Bill();
        bill.manager_uuid = actionBean.managerUuid;

        bill.bill_name = request.billName;
        bill.insurance_company_id = request.insuranceCompanyId;
        bill.principal = request.principal;
        bill.remark = request.remark;
        bill.created_at = time;
        bill.updated_at = time;
        bill.state = "1";

        Bill billByBillName = billDao.findBillByBillName(bill);
        if (billByBillName != null) {
            return json(BaseResponse.CODE_FAILURE, "结算单'" + request.billName + "'已存在", response);
        }

        bill.bill_uuid = String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());

        DealBillInsurancePolicyList dealBillInsurancePolicyList = dealBillInsurancePolicyList(bill.bill_uuid, request.warrantyList);

        if (!dealBillInsurancePolicyList.isSuccess) {
            return json(BaseResponse.CODE_FAILURE, dealBillInsurancePolicyList.message, response);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        bill.billDetailList = dealBillInsurancePolicyList.result;
        bill.is_settlement = (dealBillInsurancePolicyList.result != null && !dealBillInsurancePolicyList.result.isEmpty()) ? "1" : "0";
        bill.bill_money = decimalFormat.format(dealBillInsurancePolicyList.brokerage.doubleValue());

        int i = billDao.addBill(bill);

        String str;
        if (i > 0) {
            response.data = bill.bill_uuid;
            str = json(BaseResponse.CODE_SUCCESS, "添加结算单成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "添加结算单失败", response);
        }

        return str;
    }

    public String addBillDetail(ActionBean actionBean) {
        BillBean.AddBillDetailRequest request = JsonKit.json2Bean(actionBean.body, BillBean.AddBillDetailRequest.class);
        BillBean.AddBillDetailResponse response = new BillBean.AddBillDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

        if (request.warrantyList == null || request.warrantyList.isEmpty()) {
            return json(BaseResponse.CODE_FAILURE, "无可结算数据", response);
        }

        DealBillInsurancePolicyList dealBillInsurancePolicyList = dealBillInsurancePolicyList(request.billUuid, request.warrantyList);

        if (!dealBillInsurancePolicyList.isSuccess) {
            return json(BaseResponse.CODE_FAILURE, dealBillInsurancePolicyList.message, response);
        }

        if (dealBillInsurancePolicyList.result != null && !dealBillInsurancePolicyList.result.isEmpty()) {
            BillDetail billDetail = new BillDetail();
            billDetail.warranty_list = new ArrayList<>();
            for (BillDetail detailModel : dealBillInsurancePolicyList.result) {
                billDetail.warranty_list.add(detailModel.warranty_uuid);
            }

            BillDetail billDetailByWarrantyUuids = billDetailDao.findBillDetailByWarrantyUuids(billDetail);

            if (billDetailByWarrantyUuids != null) {
                String str = "";
                if (StringKit.equals(billDetailByWarrantyUuids.type, BillDetail.TYPE_ON_LINE)) {
                    CustWarranty insurancePolicyDetailByWarrantyUuid = custWarrantyDao.findInsurancePolicyDetailByWarrantyUuid(billDetailByWarrantyUuids.warranty_uuid);
                    if (insurancePolicyDetailByWarrantyUuid != null) {
                        str = insurancePolicyDetailByWarrantyUuid.warranty_code;
                    }
                } else if (StringKit.equals(billDetailByWarrantyUuids.type, BillDetail.TYPE_OFF_LINE)) {
                    OfflineCustWarranty offlineInsurancePolicyByWarrantyUuid = offlineCustWarrantyDao.findOfflineInsurancePolicyByWarrantyUuid(billDetailByWarrantyUuids.warranty_uuid);
                    if (offlineInsurancePolicyByWarrantyUuid != null) {
                        str = offlineInsurancePolicyByWarrantyUuid.warranty_code;
                    }
                }
                return json(BaseResponse.CODE_FAILURE, "结算单'" + str + "'已存在", response);
            }
        }

        if (StringKit.isNumeric(billByBillUuid.bill_money)) {
            dealBillInsurancePolicyList.brokerage = new BigDecimal(billByBillUuid.bill_money).add(dealBillInsurancePolicyList.brokerage);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String brokerage = decimalFormat.format(dealBillInsurancePolicyList.brokerage.doubleValue());

        int i = billDao.addBillDetailAndBrokerage(request.billUuid, brokerage, dealBillInsurancePolicyList.result);

        String str;
        if (i > 0) {
            str = json(BaseResponse.CODE_SUCCESS, "添加结算单明细成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "添加结算单明细失败", response);
        }

        return str;
    }

    public String getBillEnableInsurancePolicyList(ActionBean actionBean) {
        BillBean.GetBillEnableInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, BillBean.GetBillEnableInsurancePolicyListRequest.class);
        BillBean.GetBillEnableInsurancePolicyListResponse response = new BillBean.GetBillEnableInsurancePolicyListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (StringKit.isEmpty(request.searchType)) {
            request.searchKey = "";
        } else {
            switch (request.searchType) {
                case "1":
                case "3":
                    if (StringKit.isEmpty(request.searchKey)) {
                        return json(BaseResponse.CODE_FAILURE, "关键字不能为空", response);
                    }
                    break;
                default:
                    return json(BaseResponse.CODE_FAILURE, "搜索类型不正确", response);
            }
        }

        if (StringKit.isEmpty(request.timeType)) {
            request.startTime = "";
            request.endTime = "";
        } else {
            switch (request.timeType) {
                case "1":
                case "2":
                    if (!StringKit.isInteger(request.startTime) && !StringKit.isInteger(request.endTime)) {
                        request.timeType = null;
                        request.startTime = "";
                        request.endTime = "";

                        break;

                    }else if(!StringKit.isInteger(request.startTime)){
                        request.startTime = "0";
                    }else if(!StringKit.isInteger(request.endTime)){
                        request.endTime = String.valueOf(TimeKit.MAX_MILLIS);
                    }
                    if (Long.valueOf(request.startTime) >= Long.valueOf(request.endTime)) {
                        return json(BaseResponse.CODE_FAILURE, "开始时间不能晚于或等于结束时间", response);
                    }



                    break;
                default:
                    return json(BaseResponse.CODE_FAILURE, "时间类型不正确", response);
            }
        }


        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

        if (!StringKit.isInteger(billByBillUuid.insurance_company_id)) {
            return json(BaseResponse.CODE_FAILURE, "保险公司ID错误", response);
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        InsuranceCompanyBean company = companyClient.getCompany(Long.valueOf(billByBillUuid.insurance_company_id));

        if (company == null) {
            return json(BaseResponse.CODE_FAILURE, "保险公司'" + billByBillUuid.insurance_company_id + "'未找到", response);
        }

        long total;
        response.data = new ArrayList<>();

        switch (request.type) {
            case BillDetail.TYPE_ON_LINE:
                CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
                custWarrantyCost.search = request.searchKey;
                custWarrantyCost.searchType = request.searchType;
                custWarrantyCost.ins_company_id = billByBillUuid.insurance_company_id;
                custWarrantyCost.manager_uuid = actionBean.managerUuid;
                custWarrantyCost.time_type = request.timeType;
                custWarrantyCost.start_time = request.startTime;
                custWarrantyCost.end_time = request.endTime;
                custWarrantyCost.page = setPage(request.lastId, request.pageNum, request.pageSize);

                Set<String> productIds = new HashSet<>();
                Set<String> companyIds = new HashSet<>();

                List<CustWarrantyCost> completePayListByManagerUuid = custWarrantyCostDao.findCompletePayListByManagerUuid(custWarrantyCost);
                if (completePayListByManagerUuid != null && !completePayListByManagerUuid.isEmpty()) {
                    for (CustWarrantyCost warrantyCostModel : completePayListByManagerUuid) {
                        companyIds.add(warrantyCostModel.ins_company_id);
                        productIds.add(warrantyCostModel.product_id);
                        response.data.add(new BillBean.BillInsurancePolicy(warrantyCostModel));
                    }

                    List<ProductBean> productList = productClient.getProductList(new ArrayList<>(productIds));
                    Map<String, ProductBean> productMap = new HashMap<>();
                    if (productList != null && !productList.isEmpty()) {
                        for (ProductBean productBean : productList) {
                            productMap.put(String.valueOf(productBean.id), productBean);
                        }
                    }

                    List<InsuranceCompanyBean> companyList = companyClient.getCompanyList(new ArrayList<>(companyIds));
                    Map<String, InsuranceCompanyBean> companyMap = new HashMap<>();
                    if (companyList != null && !companyList.isEmpty()) {
                        for (InsuranceCompanyBean insuranceCompanyBean : companyList) {
                            companyMap.put(String.valueOf(insuranceCompanyBean.id), insuranceCompanyBean);
                        }
                    }

                    for (BillBean.BillInsurancePolicy datum : response.data) {
                        ProductBean productBean = productMap.get(datum.productId);
                        if (productBean != null) {
                            datum.productName = productBean.name;
                        }

                        InsuranceCompanyBean insuranceCompanyBean = companyMap.get(datum.companyId);
                        if (insuranceCompanyBean != null) {
                            datum.companyName = insuranceCompanyBean.name;
                        }
                    }
                }

                total = custWarrantyCostDao.findCompletePayCountByManagerUuid(custWarrantyCost);
                break;
            case BillDetail.TYPE_OFF_LINE:
                OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
                offlineCustWarranty.search = request.searchKey;
                offlineCustWarranty.searchType = request.searchType;
                offlineCustWarranty.insurance_company = company.name;
                offlineCustWarranty.manager_uuid = actionBean.managerUuid;
                offlineCustWarranty.time_type = request.timeType;
                offlineCustWarranty.start_time = request.startTime;
                offlineCustWarranty.end_time = request.endTime;
                offlineCustWarranty.page = setPage(request.lastId, request.pageNum, request.pageSize);

                List<OfflineCustWarranty> completePayListByManagerUuid1 = offlineCustWarrantyDao.findCompletePayListByManagerUuid(offlineCustWarranty);
                if (completePayListByManagerUuid1 != null && !completePayListByManagerUuid1.isEmpty()) {
                    for (OfflineCustWarranty offlineCustWarranty1 : completePayListByManagerUuid1) {
                        BillBean.BillInsurancePolicy insurancePolicy = new BillBean.BillInsurancePolicy(offlineCustWarranty1);
                        if("14463303497682968".equals(actionBean.managerUuid)){
                            insurancePolicy.insuredName = "***";
                        }
                        response.data.add(insurancePolicy);
                    }
                }

                total = offlineCustWarrantyDao.findCompletePayCountByManagerUuid(offlineCustWarranty);
                break;
            default:
                return json(BaseResponse.CODE_PARAM_ERROR, "保单类型错误", response);
        }

        String lastId = "0";
        if (!response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);
    }

    public String getBillList(ActionBean actionBean) {
        BillBean.GetBillListRequest request = JsonKit.json2Bean(actionBean.body, BillBean.GetBillListRequest.class);
        BillBean.GetBillListResponse response = new BillBean.GetBillListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        Bill bill = new Bill();
        bill.manager_uuid = actionBean.managerUuid;

        if (StringKit.isEmpty(request.searchType)) {
            request.searchKey = "";
        } else {
            if (StringKit.isEmpty(request.searchKey)) {
                return json(BaseResponse.CODE_FAILURE, "关键字不能为空", response);
            }

            bill.searchType = request.searchType;
            switch (request.searchType) {
                case "1":
                    InsuranceCompanyBean insuranceCompanyBean = new InsuranceCompanyBean();
                    insuranceCompanyBean.managerUuid = actionBean.managerUuid;
                    insuranceCompanyBean.name = request.searchKey;
                    List<InsuranceCompanyBean> listInsuranceCompany = companyClient.getListInsuranceCompany(insuranceCompanyBean);


                    if (listInsuranceCompany != null && !listInsuranceCompany.isEmpty()) {
                        StringBuilder sb1 = new StringBuilder();
                        int size = listInsuranceCompany.size();
                        for (int i = 0; i < size; i++) {
                            InsuranceCompanyBean insuranceCompanyBean1 = listInsuranceCompany.get(i);
                            sb1.append(insuranceCompanyBean1.id);
                            if (i != size - 1) {
                                sb1.append(",");
                            }
                        }
                        bill.insurance_company_id_string = "";
                    } else {
                        response.data = new ArrayList<>();
                        response.page = setPageBean("0", request.pageNum, request.pageSize, 0, 0);
                        return json(BaseResponse.CODE_SUCCESS, "获取结算单列表成功", response);
                    }

                    break;
                case "2":
                    bill.search = request.searchKey;
                    break;
            }
        }

        bill.is_settlement = request.billStatus;

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        bill.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<Bill> billByManagerUuid = billDao.findBillByManagerUuid(bill);
        long total = billDao.findBillCountByManagerUuid(bill);
        response.data = new ArrayList<>();

        Map<String, String> agentName = new HashMap<>();
        Map<String, String> companyName = new HashMap<>();

        if (billByManagerUuid != null && !billByManagerUuid.isEmpty()) {
            for (Bill model : billByManagerUuid) {

                //根据负责人ID获取负责人名称
                String s = agentName.get(model.principal);
                if (s == null && StringKit.isInteger(model.principal)) {
                    AgentBean agentById = agentClient.getAgentById(Long.valueOf(model.principal));
                    if (agentById != null) {
                        model.principal = agentById.name;
                        agentName.put(model.principal, agentById.name);
                    } else {
                        agentName.put(model.principal, "");
                    }
                } else {
                    model.principal = s;
                }

                String s1 = companyName.get(model.insurance_company_id);
                if (s1 == null && StringKit.isInteger(model.insurance_company_id)) {
                    InsuranceCompanyBean company = companyClient.getCompany(Long.valueOf(model.insurance_company_id));
                    if (company != null) {
                        model.insurance_company_name = company.name;
                        companyName.put(model.insurance_company_id, company.name);
                    } else {
                        companyName.put(model.insurance_company_id, "");
                    }
                } else {
                    model.insurance_company_name = s1;
                }
                response.data.add(new BillBean.BillData(model));
            }
        }

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取结算单列表成功", response);
    }

    public String getBillInfo(ActionBean actionBean) {
        BillBean.GetBillInfoRequest request = JsonKit.json2Bean(actionBean.body, BillBean.GetBillInfoRequest.class);
        BillBean.GetBillInfoResponse response = new BillBean.GetBillInfoResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

        if (StringKit.isInteger(billByBillUuid.insurance_company_id)) {
            InsuranceCompanyBean company = companyClient.getCompany(Long.valueOf(billByBillUuid.insurance_company_id));

            if (company != null) {
                billByBillUuid.insurance_company_name = company.name;
            }
        }

        response.data = new BillBean.BillData(billByBillUuid);

        return json(BaseResponse.CODE_SUCCESS, "获取结算单信息成功", response);
    }

    public String getBillDetail(ActionBean actionBean) {
        BillBean.GetBillDetailRequest request = JsonKit.json2Bean(actionBean.body, BillBean.GetBillDetailRequest.class);
        BillBean.GetBillDetailResponse response = new BillBean.GetBillDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        BillDetail billDetail = new BillDetail();
        billDetail.bill_uuid = request.billUuid;

        if (StringKit.isEmpty(request.searchType)) {
            request.searchKey = "";
        } else {
            switch (request.searchType) {
                case "1":
                case "2":
                case "3":
                    if (StringKit.isEmpty(request.searchKey)) {
                        return json(BaseResponse.CODE_FAILURE, "关键字不能为空", response);
                    }

                    if (StringKit.equals(request.searchType, "2")) {
                        List<ProductBean> listProduct = productClient.getListProduct(request.searchKey, actionBean.managerUuid);

                        if (listProduct != null && !listProduct.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            int size = listProduct.size();
                            for (int i = 0; i < size; i++) {
                                ProductBean productBean = listProduct.get(i);
                                sb.append(productBean.id);
                                if (i != size - 1) {
                                    sb.append(",");
                                }
                            }
                            billDetail.product_id_string = sb.toString();
                        }
                    }

                    billDetail.search = request.searchKey;
                    billDetail.searchType = request.searchType;
                    break;
                default:
                    return json(BaseResponse.CODE_FAILURE, "搜索类型不正确", response);
            }
        }

        billDetail.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<BillDetail> billDetailByBillUuid = billDetailDao.findBillDetailByBillUuid(billDetail);

        long total = billDetailDao.findBillDetailCountByBillUuid(billDetail);
        response.data = dealBillDetailModelList(billDetailByBillUuid,actionBean.managerUuid);

        String lastId = "0";
        if (!response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取结算单明细成功", response);
    }

    public String downloadBillDetail(ActionBean actionBean) {
        BillBean.DownloadBillDetailRequest request = JsonKit.json2Bean(actionBean.body, BillBean.DownloadBillDetailRequest.class);
        BillBean.DownloadBillDetailResponse response = new BillBean.DownloadBillDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        BillDetail billDetail = new BillDetail();
        billDetail.bill_uuid = request.billUuid;
        billDetail.page = setPage("0", null, "1000");

        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(billByBillUuid.bill_name + "-结算单明细");

        List<ExcelModel<BillBean.BillInsurancePolicy>> list = new ArrayList<>();
        BillBean.BillInsurancePolicy downloadBillDetailTitle = BillBean.getDownloadBillDetailTitle();
        ExcelModel<BillBean.BillInsurancePolicy> title = new ExcelModel<>(downloadBillDetailTitle, true, "title");
        title.boldWeight = Font.BOLDWEIGHT_BOLD;
        list.add(title);
        int startRow = 0;

        Map<String, String> columnFieldMap = ExcelModelKit.getColumnFieldMap(BillBean.BILL_DETAIL_LIST, 0);
        Map<String, CellStyle> cellStyleMap = ExcelModelKit.getCellStyleMap();

        int i = ExcelModelKit.writeRank(sheet, list, columnFieldMap, startRow, cellStyleMap, new HashMap<>());
        startRow += i;

        boolean flag;
        do {
            List<BillDetail> billDetailByBillUuid = billDetailDao.findBillDetailByBillUuid(billDetail);

            List<BillBean.BillInsurancePolicy> billInsurancePolicies = dealBillDetailModelList(billDetailByBillUuid,actionBean.managerUuid);

            if (!billInsurancePolicies.isEmpty()) {
                list.clear();
                for (BillBean.BillInsurancePolicy billInsurancePolicy : billInsurancePolicies) {
                    list.add(new ExcelModel<>(billInsurancePolicy));
                }
                startRow = ExcelModelKit.writeRank(sheet, list, columnFieldMap, startRow, cellStyleMap, BillBean.BILL_DETAIL_FIELD_TYPE);
                billDetail.page = setPage(billInsurancePolicies.get(billInsurancePolicies.size() - 1).id, null, "1000");
            }

            flag = billDetailByBillUuid != null && billDetailByBillUuid.size() >= 1000;
        } while (flag);

        ExcelModelKit.autoSizeColumn(sheet, columnFieldMap.size());

        byte[] workbookByteArray = ExcelModelKit.getWorkbookByteArray(workbook);

        if (workbookByteArray == null) {
            return json(BaseResponse.CODE_FAILURE, "生成表格失败", response);
        }

        FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
        // fileUploadRequest.base64 = Base64.getEncoder().encodeToString(workbookByteArray);
        fileUploadRequest.fileKey = MD5Kit.MD5Digest(billByBillUuid.bill_name + actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
        fileUploadRequest.fileName = fileUploadRequest.fileKey + ".xls";
        boolean upload = fileClient.upload(fileUploadRequest.fileKey, fileUploadRequest.fileName, workbookByteArray);

        if (upload) {
            response.data = fileClient.getFileUrl(fileUploadRequest.fileKey);
            if (StringKit.isEmpty(response.data)) {
                return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
        }

        return json(BaseResponse.CODE_SUCCESS, "获取下载地址成功", response);
    }

    public String deleteBill(ActionBean actionBean) {
        BillBean.DeleteBillRequest request = JsonKit.json2Bean(actionBean.body, BillBean.DeleteBillRequest.class);
        BillBean.DeleteBillResponse response = new BillBean.DeleteBillResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

//        if (StringKit.equals(billByBillUuid.is_settlement, BillModel.SETTLEMENT_STATE_ALREADY)) {
//            return json(BaseResponse.CODE_FAILURE, "该结算单已结算，不能删除修改明细信息", response);
//        }

        int i = billDao.deleteBill(billByBillUuid);

        String str;
        if (i > 0) {
            str = json(BaseResponse.CODE_SUCCESS, "删除结算单成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "删除结算单失败", response);
        }

        return str;
    }

    public String deleteBillDetail(ActionBean actionBean) {
        BillBean.DeleteBillDetailRequest request = JsonKit.json2Bean(actionBean.body, BillBean.DeleteBillDetailRequest.class);
        BillBean.DeleteBillDetailResponse response = new BillBean.DeleteBillDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (request.billDetails == null || request.billDetails.isEmpty()) {
            return json(BaseResponse.CODE_FAILURE, "无可删除数据", response);
        }

        Bill billByBillUuid = billDao.findBillByBillUuid(request.billUuid);

        if (billByBillUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
        }

//        if (StringKit.equals(billByBillUuid.is_settlement, BillModel.SETTLEMENT_STATE_ALREADY)) {
//            return json(BaseResponse.CODE_FAILURE, "该结算单已结算，不能删除修改明细信息", response);
//        }

        BigDecimal bigDecimal = new BigDecimal(0);
        List<BillDetail> list = new ArrayList<>();
        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
        OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();

        for (BillBean.BillInsurancePolicy billDetail : request.billDetails) {
            BillDetail billDetailById = billDetailDao.findBillDetailById(billDetail.id);
            BigDecimal brokerage;
            if (billDetailById != null) {
                list.add(billDetailById);
                if (StringKit.equals(billDetailById.type, BillDetail.TYPE_ON_LINE)) {
                    custWarrantyCost.id = billDetail.costId;
                    CustWarrantyBrokerage brokerageByCostId = custWarrantyCostDao.findBrokerageByCostId(custWarrantyCost);

                    if (brokerageByCostId != null && StringKit.isNumeric(brokerageByCostId.manager_money)) {
                        brokerage = new BigDecimal(brokerageByCostId.manager_money);
                    } else {
                        brokerage = new BigDecimal("0");
                    }

                } else if (StringKit.equals(billDetailById.type, BillDetail.TYPE_OFF_LINE)) {
                    offlineCustWarranty.warranty_uuid = billDetailById.warranty_uuid;
                    OfflineCustWarranty brokerageByWarrantyUuid = offlineCustWarrantyDao.findBrokerageByWarrantyUuid(offlineCustWarranty);

                    if (brokerageByWarrantyUuid != null && StringKit.isNumeric(brokerageByWarrantyUuid.brokerage)) {
                        brokerage = new BigDecimal(brokerageByWarrantyUuid.brokerage);
                    } else {
                        brokerage = new BigDecimal("0");
                    }

                } else {
                    return json(BaseResponse.CODE_FAILURE, "数据不存在", response);
                }

                bigDecimal = bigDecimal.add(brokerage);
            } else {
                return json(BaseResponse.CODE_FAILURE, "数据不存在", response);
            }
        }

        int i = billDao.deleteBillDetailByIds(list, request.billUuid, bigDecimal);

        String str;
        if (i > 0) {
            str = json(BaseResponse.CODE_SUCCESS, "修改结算单明细成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "修改结算单明细失败", response);
        }

        return str;
    }


//    public String clearingBill(ActionBean actionBean) {
//        Bill.ClearingBillRequest request = JsonKit.json2Bean(actionBean.body, Bill.ClearingBillRequest.class);
//        Bill.ClearingBillResponse response = new Bill.ClearingBillResponse();
//
//        if (request == null) {
//            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
//        }
//
//        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
//        if (entries != null) {
//            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
//        }
//
//        BillModel billByBillUuid = billDao.findBillByBillUuid(request.billUuid);
//
//        if (billByBillUuid == null) {
//            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
//        }
//
//        if (StringKit.equals(billByBillUuid.is_settlement, BillModel.SETTLEMENT_STATE_ALREADY)) {
//            return json(BaseResponse.CODE_FAILURE, "该结算单已结算，不能重复结算", response);
//        }
//
//        int i = billDao.clearingBill(billByBillUuid);
//
//        String str;
//        if (i > 0) {
//            str = json(BaseResponse.CODE_SUCCESS, "结算成功", response);
//        } else {
//            str = json(BaseResponse.CODE_FAILURE, "结算失败", response);
//        }
//
//        return str;
//    }
//
//    public String cancelClearingBill(ActionBean actionBean) {
//        Bill.CancelClearingBillRequest request = JsonKit.json2Bean(actionBean.body, Bill.CancelClearingBillRequest.class);
//        Bill.CancelClearingBillResponse response = new Bill.CancelClearingBillResponse();
//
//        if (request == null) {
//            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
//        }
//
//        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
//        if (entries != null) {
//            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
//        }
//
//        BillModel billByBillUuid = billDao.findBillByBillUuid(request.billUuid);
//
//        if (billByBillUuid == null) {
//            return json(BaseResponse.CODE_FAILURE, "结算单不存在", response);
//        }
//
//        if (!StringKit.equals(billByBillUuid.is_settlement, BillModel.SETTLEMENT_STATE_ALREADY)) {
//            return json(BaseResponse.CODE_FAILURE, "该结算单未结算，不能执行取消结算操作", response);
//        }
//
//        int i = billDao.cancelClearingBill(billByBillUuid);
//
//        String str;
//        if (i > 0) {
//            str = json(BaseResponse.CODE_SUCCESS, "取消结算成功", response);
//        } else {
//            str = json(BaseResponse.CODE_FAILURE, "取消结算失败", response);
//        }
//
//        return str;
//    }

    private List<BillBean.BillInsurancePolicy> dealBillDetailModelList(List<BillDetail> list, String managerUuid) {
        List<BillBean.BillInsurancePolicy> result = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return result;
        }

        Set<String> productIds = new HashSet<>();
        Set<String> companyIds = new HashSet<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        for (BillDetail detailModel : list) {
            BillBean.BillInsurancePolicy billInsurancePolicy = new BillBean.BillInsurancePolicy();

            billInsurancePolicy.id = detailModel.id;
            billInsurancePolicy.warrantyUuid = detailModel.warranty_uuid;
            billInsurancePolicy.costId = detailModel.cost_id;
            billInsurancePolicy.warrantyType = detailModel.type;
            billInsurancePolicy.warrantyTypeText = detailModel.typeText(detailModel.type);

            billInsurancePolicy.billTime = detailModel.bill_time;
            if (StringKit.isInteger(detailModel.bill_time)) {
                billInsurancePolicy.billTimeText = sdf.format(new Date(Long.valueOf(detailModel.bill_time)));
            }

            billInsurancePolicy.createdAt = detailModel.created_at;
            if (StringKit.isInteger(detailModel.created_at)) {
                billInsurancePolicy.createdAtText = sdf.format(new Date(Long.valueOf(detailModel.created_at)));
            }

            if (StringKit.equals(detailModel.type, BillDetail.TYPE_ON_LINE)) {
                billInsurancePolicy.warrantyCode = detailModel.online_warranty_code;
                billInsurancePolicy.productId = detailModel.online_product_id;
                billInsurancePolicy.companyId = detailModel.online_ins_company_id;
                productIds.add(detailModel.online_product_id);
                companyIds.add(detailModel.online_ins_company_id);





                billInsurancePolicy.premium = detailModel.online_premium;
                if (StringKit.isNumeric(detailModel.online_premium)) {
                    billInsurancePolicy.premium = decimalFormat.format(new BigDecimal(detailModel.online_premium).doubleValue());
                } else {
                    billInsurancePolicy.premium = "0.00";
                }
                billInsurancePolicy.premiumText = String.format("¥%s", billInsurancePolicy.premium);

                billInsurancePolicy.fee = detailModel.online_brokerage_manager_money;
                if (StringKit.isNumeric(detailModel.online_brokerage_manager_money)) {
                    billInsurancePolicy.fee = decimalFormat.format(new BigDecimal(detailModel.online_brokerage_manager_money).doubleValue());
                } else {
                    billInsurancePolicy.fee = "0.00";
                }
                billInsurancePolicy.feeText = String.format("¥%s", billInsurancePolicy.fee);

                billInsurancePolicy.feeRate = detailModel.online_brokerage_manager_rate;
                if (StringKit.isNumeric(detailModel.online_brokerage_manager_rate)) {
                    BigDecimal bigDecimal = new BigDecimal(detailModel.online_brokerage_manager_rate);
                    billInsurancePolicy.feeRate = decimalFormat.format(bigDecimal.doubleValue());
                    bigDecimal = bigDecimal.multiply(new BigDecimal(100));
                    billInsurancePolicy.feeRateText = decimalFormat.format(bigDecimal) + "%";
                } else {
                    billInsurancePolicy.feeRate = "0.00";
                    billInsurancePolicy.feeRateText = "0.00%";
                }
                billInsurancePolicy.feeRatePercentage = decimalFormat.format(new BigDecimal(billInsurancePolicy.feeRate).multiply(new BigDecimal(100)).doubleValue());
                billInsurancePolicy.startTime = detailModel.online_start_time;
                if (StringKit.isInteger(detailModel.online_start_time)) {
                    billInsurancePolicy.startTimeText = sdf.format(new Date(Long.valueOf(detailModel.online_start_time)));
                }
                billInsurancePolicy.endTime = detailModel.online_end_time;
                if (StringKit.isInteger(detailModel.online_end_time)) {
                    billInsurancePolicy.endTimeText = sdf.format(new Date(Long.valueOf(detailModel.online_end_time)));
                }
                billInsurancePolicy.orderTime = detailModel.online_created_at;
                if (StringKit.isInteger(detailModel.online_created_at)) {
                    billInsurancePolicy.orderTimeText = sdf.format(new Date(Long.valueOf(detailModel.online_created_at)));
                }
                billInsurancePolicy.phase = detailModel.online_phase;

            } else if (StringKit.equals(detailModel.type, BillDetail.TYPE_OFF_LINE)) {
                billInsurancePolicy.warrantyCode = detailModel.offline_warranty_code;
                billInsurancePolicy.companyName = detailModel.offline_insurance_company;
                billInsurancePolicy.productName = detailModel.offline_insurance_product;
                billInsurancePolicy.insuredName = detailModel.offline_insured_name;

                billInsurancePolicy.fee = detailModel.offline_brokerage;
                if (StringKit.isNumeric(detailModel.offline_brokerage)) {
                    billInsurancePolicy.fee = decimalFormat.format(new BigDecimal(detailModel.offline_brokerage).doubleValue());
                } else {
                    billInsurancePolicy.fee = "0.00";
                }
                billInsurancePolicy.feeText = String.format("¥%s", billInsurancePolicy.fee);

                if (StringKit.isNumeric(detailModel.offline_premium)) {
                    BigDecimal bigDecimal = new BigDecimal(detailModel.offline_premium);
                    billInsurancePolicy.premium = decimalFormat.format(bigDecimal.doubleValue());
                    if (bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal divide = new BigDecimal(billInsurancePolicy.fee).divide(bigDecimal, 6, BigDecimal.ROUND_HALF_DOWN);
                        billInsurancePolicy.feeRate = decimalFormat.format(divide.doubleValue());
                        BigDecimal multiply = divide.multiply(new BigDecimal("100"));
                        billInsurancePolicy.feeRateText = decimalFormat.format(multiply.doubleValue()) + "%";
                    } else {
                        billInsurancePolicy.feeRate = "0";
                        billInsurancePolicy.feeRateText = "0.00%";
                    }
                } else {
                    billInsurancePolicy.premium = "0.00";
                    billInsurancePolicy.feeRate = "0";
                    billInsurancePolicy.feeRateText = "0.00%";
                }
                billInsurancePolicy.premiumText = "¥" + billInsurancePolicy.premium;
                billInsurancePolicy.feeRatePercentage = decimalFormat.format(new BigDecimal(billInsurancePolicy.feeRate).multiply(new BigDecimal(100)).doubleValue());
                billInsurancePolicy.startTime = detailModel.offline_start_time;
                if (StringKit.isInteger(detailModel.offline_start_time)) {
                    billInsurancePolicy.startTimeText = sdf.format(new Date(Long.valueOf(detailModel.offline_start_time)));
                }
                billInsurancePolicy.endTime = detailModel.offline_end_time;
                if (StringKit.isInteger(detailModel.offline_end_time)) {
                    billInsurancePolicy.endTimeText = sdf.format(new Date(Long.valueOf(detailModel.offline_end_time)));
                }
                billInsurancePolicy.orderTime = detailModel.offline_order_time;
                if (StringKit.isInteger(detailModel.offline_order_time)) {
                    billInsurancePolicy.orderTimeText = sdf.format(new Date(Long.valueOf(detailModel.offline_order_time)));
                }
                billInsurancePolicy.phase = detailModel.offline_payment_time;

            } else {
                continue;
            }
            if("14463303497682968".equals(managerUuid)){
                billInsurancePolicy.insuredName = "***";
            }
            billInsurancePolicy.type = detailModel.online_type;
            result.add(billInsurancePolicy);
        }

        List<ProductBean> productList = productClient.getProductList(new ArrayList<>(productIds));
        Map<String, ProductBean> productMap = new HashMap<>();
        if (productList != null && !productList.isEmpty()) {
            for (ProductBean productBean : productList) {
                productMap.put(String.valueOf(productBean.id), productBean);
            }
        }

        List<InsuranceCompanyBean> companyList = companyClient.getCompanyList(new ArrayList<>(companyIds));
        Map<String, InsuranceCompanyBean> companyMap = new HashMap<>();
        if (companyList != null && !companyList.isEmpty()) {
            for (InsuranceCompanyBean insuranceCompanyBean : companyList) {
                companyMap.put(String.valueOf(insuranceCompanyBean.id), insuranceCompanyBean);
            }
        }

        for (BillBean.BillInsurancePolicy datum : result) {
            if (StringKit.equals(datum.warrantyType, BillDetail.TYPE_ON_LINE)) {
                ProductBean productBean = productMap.get(datum.productId);
                if (productBean != null) {
                    datum.productName = productBean.name;
                }

                InsuranceCompanyBean insuranceCompanyBean = companyMap.get(datum.companyId);
                if (insuranceCompanyBean != null) {
                    datum.companyName = insuranceCompanyBean.name;
                }
            }
        }

        return result;
    }

    static class DealBillInsurancePolicyList {
        boolean isSuccess;
        List<BillDetail> result;
        BigDecimal brokerage;
        String message;
    }

    private DealBillInsurancePolicyList dealBillInsurancePolicyList(String bill_uuid, List<BillBean.BillInsurancePolicy> list) {

        DealBillInsurancePolicyList dealBillInsurancePolicyList = new DealBillInsurancePolicyList();

        dealBillInsurancePolicyList.isSuccess = true;
        dealBillInsurancePolicyList.result = new ArrayList<>();
        dealBillInsurancePolicyList.brokerage = new BigDecimal(0);

        if (list != null && !list.isEmpty()) {
            String time = String.valueOf(System.currentTimeMillis());
            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
            OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();

            for (BillBean.BillInsurancePolicy billInsurancePolicy : list) {

                BillDetail billDetail = new BillDetail();
                billDetail.bill_uuid = bill_uuid;
                billDetail.cost_id = billInsurancePolicy.costId;
                billDetail.warranty_uuid = billInsurancePolicy.warrantyUuid;
                billDetail.type = billInsurancePolicy.warrantyType;
                billDetail.created_at = time;
                billDetail.updated_at = time;
                dealBillInsurancePolicyList.result.add(billDetail);
                BigDecimal brokerage;

                if (StringKit.equals(billInsurancePolicy.warrantyType, BillDetail.TYPE_ON_LINE)) {
                    if (StringKit.isEmpty(billInsurancePolicy.costId)) {
                        dealBillInsurancePolicyList.isSuccess = false;
                        dealBillInsurancePolicyList.message = "网销保单'" + billDetail.warranty_uuid + "'必须提供costId";
                        return dealBillInsurancePolicyList;
                    }
                    custWarrantyCost.id = billInsurancePolicy.costId;
                    CustWarrantyBrokerage brokerageByCostId = custWarrantyCostDao.findBrokerageByCostId(custWarrantyCost);

                    if (brokerageByCostId != null && StringKit.isNumeric(brokerageByCostId.manager_money)) {
                        brokerage = new BigDecimal(brokerageByCostId.manager_money);
                    } else {
                        brokerage = new BigDecimal("0");
                    }
                } else if (StringKit.equals(billInsurancePolicy.warrantyType, BillDetail.TYPE_OFF_LINE)) {
                    offlineCustWarranty.warranty_uuid = billInsurancePolicy.warrantyUuid;
                    OfflineCustWarranty brokerageByWarrantyUuid = offlineCustWarrantyDao.findBrokerageByWarrantyUuid(offlineCustWarranty);

                    if (brokerageByWarrantyUuid != null && StringKit.isNumeric(brokerageByWarrantyUuid.brokerage)) {
                        brokerage = new BigDecimal(brokerageByWarrantyUuid.brokerage);
                    } else {
                        brokerage = new BigDecimal("0");
                    }
                } else {
                    dealBillInsurancePolicyList.isSuccess = false;
                    dealBillInsurancePolicyList.message = "保单类型错误";
                    return dealBillInsurancePolicyList;
                }

                dealBillInsurancePolicyList.brokerage = dealBillInsurancePolicyList.brokerage.add(brokerage);
            }
        }

        return dealBillInsurancePolicyList;
    }

}
