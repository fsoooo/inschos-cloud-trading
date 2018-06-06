package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.rpc.bean.AgentBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.client.AgentClient;
import com.inschos.cloud.trading.access.rpc.client.FileClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.*;
import com.inschos.cloud.trading.data.dao.*;
import com.inschos.cloud.trading.extend.file.FileUpload;
import com.inschos.cloud.trading.extend.file.FileUploadResponse;
import com.inschos.cloud.trading.model.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 创建日期：2018/3/22 on 11:06
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class InsurancePolicyAction extends BaseAction {

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    @Autowired
    private CarInfoDao carInfoDao;

    @Autowired
    private InsuranceParticipantDao insuranceParticipantDao;

    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;

    @Autowired
    private OfflineInsurancePolicyDao offlineInsurancePolicyDao;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private FileClient fileClient;

    public String getInsurancePolicyStatusList(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatusListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatusListRequest.class);
        InsurancePolicy.GetInsurancePolicyStatusListResponse response = new InsurancePolicy.GetInsurancePolicyStatusListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        response.data = new ArrayList<>();
        // 1-待支付 2-待生效 3-保障中 4-已失效
        InsurancePolicy.GetInsurancePolicyStatus bean1 = new InsurancePolicy.GetInsurancePolicyStatus();
        bean1.value = "1";
        bean1.valueText = "待支付";
        response.data.add(bean1);

        InsurancePolicy.GetInsurancePolicyStatus bean2 = new InsurancePolicy.GetInsurancePolicyStatus();
        bean2.value = "2";
        bean2.valueText = "待生效";
        response.data.add(bean2);

        InsurancePolicy.GetInsurancePolicyStatus bean3 = new InsurancePolicy.GetInsurancePolicyStatus();
        bean3.value = "3";
        bean3.valueText = "保障中";
        response.data.add(bean3);

        InsurancePolicy.GetInsurancePolicyStatus bean4 = new InsurancePolicy.GetInsurancePolicyStatus();
        bean4.value = "4";
        bean4.valueText = "已失效";
        response.data.add(bean4);

//        LinkedHashMap<String, String> warrantyStatusMap = InsurancePolicyModel.getWarrantyStatusMap();
//        Set<String> strings = warrantyStatusMap.keySet();
//
//        for (String string : strings) {
//            InsurancePolicy.GetInsurancePolicyStatus getInsurancePolicyStatus = new InsurancePolicy.GetInsurancePolicyStatus();
//            getInsurancePolicyStatus.value = string;
//            getInsurancePolicyStatus.valueText = warrantyStatusMap.get(string);
//            response.data.add(getInsurancePolicyStatus);
//        }

        return json(BaseResponse.CODE_SUCCESS, "获取保单状态分类成功", response);
    }

    public String getInsurancePolicySourceList(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicySourceListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicySourceListRequest.class);
        InsurancePolicy.GetInsurancePolicySourceListResponse response = new InsurancePolicy.GetInsurancePolicySourceListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        response.data = new ArrayList<>();
        LinkedHashMap<String, String> warrantyStatusMap = InsurancePolicyModel.getWarrantyFromMap();
        Set<String> strings = warrantyStatusMap.keySet();

        for (String string : strings) {
            InsurancePolicy.GetInsurancePolicyStatus getInsurancePolicyStatus = new InsurancePolicy.GetInsurancePolicyStatus();
            getInsurancePolicyStatus.value = string;
            getInsurancePolicyStatus.valueText = warrantyStatusMap.get(string);
            response.data.add(getInsurancePolicyStatus);
        }

        return json(BaseResponse.CODE_SUCCESS, "获取保单来源分类成功", response);
    }


    public String getInsurancePolicyListForOnlineStore(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListForOnlineStoreRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListForOnlineStoreRequest.class);
        InsurancePolicy.GetInsurancePolicyListForOnlineStoreResponse response = new InsurancePolicy.GetInsurancePolicyListForOnlineStoreResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (StringKit.isEmpty(request.pageNum)) {
            request.pageNum = "1";
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        String str;

        if (!StringKit.isEmpty(request.searchKey) || !StringKit.isEmpty(request.warrantyStatus)) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();

            insurancePolicyModel.account_uuid = actionBean.accountUuid;
            insurancePolicyModel.search = request.searchKey;
            insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

            // 将前台的状态转成我们需要的状态
            long total = 0;
            List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = new ArrayList<>();
            if (StringKit.isEmpty(request.warrantyStatus)) {
                insurancePolicyModel.status_string = "";
            } else {
                // 1-待支付 2-待生效 3-保障中 4-已失效
                switch (request.warrantyStatus) {
                    case "1":
                        insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_PENDING;
                        total = insurancePolicyDao.findInsurancePolicyCountForInsuring(insurancePolicyModel);
                        insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListForInsuring(insurancePolicyModel);
                        break;
                    case "2":
                        insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_WAITING;
                        total = insurancePolicyDao.findInsurancePolicyCountByWarrantyStatusString(insurancePolicyModel);
                        insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusStringOrSearch(insurancePolicyModel);
                        break;
                    case "3":
                        insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_EFFECTIVE;
                        total = insurancePolicyDao.findInsurancePolicyCountByWarrantyStatusString(insurancePolicyModel);
                        insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusStringOrSearch(insurancePolicyModel);
                        break;
                    case "4":
                        insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_INVALID + "," + InsurancePolicyModel.POLICY_STATUS_EXPIRED;
                        total = insurancePolicyDao.findInsurancePolicyCountByWarrantyStatusString(insurancePolicyModel);
                        insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusStringOrSearch(insurancePolicyModel);
                        break;
                }
            }

            response.data = new ArrayList<>();
            String lastId = "0";

            if (insurancePolicyListByWarrantyStatusOrSearch != null && !insurancePolicyListByWarrantyStatusOrSearch.isEmpty()) {

                HashMap<String, ProductBean> map = new HashMap<>();
                List<ProductBean> ciProduct = productClient.getPlatformProductAll(actionBean.managerUuid, 42);
                if (ciProduct != null) {
                    for (ProductBean productBean : ciProduct) {
                        map.put(String.valueOf(productBean.id), productBean);
                    }
                }

                List<ProductBean> biProduct = productClient.getPlatformProductAll(actionBean.managerUuid, 43);
                if (biProduct != null) {
                    for (ProductBean productBean : biProduct) {
                        map.put(String.valueOf(productBean.id), productBean);
                    }
                }

                CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
                for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyListByWarrantyStatusOrSearch) {

                    custWarrantyCostModel.warranty_uuid = policyListByWarrantyStatusOrSearch.warranty_uuid;

                    List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

                    CustWarrantyCostListResult custWarrantyCostListResult = dealCustWarrantyCostList(custWarrantyCostByWarrantyUuid);

                    InsurancePolicy.GetInsurancePolicy insurancePolicy = new InsurancePolicy.GetInsurancePolicy(policyListByWarrantyStatusOrSearch, custWarrantyCostListResult.premium, custWarrantyCostListResult.payMoney, custWarrantyCostListResult.taxMoney, custWarrantyCostListResult.warrantyStatusForPay, custWarrantyCostListResult.warrantyStatusForPayText);

                    if (StringKit.equals(insurancePolicy.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                        CarInfoModel bjCodeFlagAndBizIdByWarrantyUuid = carInfoDao.findBjCodeFlagAndBizIdByWarrantyUuid(insurancePolicy.warrantyUuid);
                        insurancePolicy.bjCodeFlag = bjCodeFlagAndBizIdByWarrantyUuid.bj_code_flag;
                        insurancePolicy.bizId = bjCodeFlagAndBizIdByWarrantyUuid.biz_id;
                    }

                    ProductBean productBean = map.get(insurancePolicy.productId);
                    if (productBean != null) {
                        insurancePolicy.insuranceProductName = productBean.displayName;
                        insurancePolicy.insuranceCompanyName = productBean.insuranceCoName;
                        String[] split = productBean.code.split("_");
                        if (split.length > 1) {
                            insurancePolicy.insuranceCompanyLogo = fileClient.getFileUrl("property_key_" + split[0]);
                        }
                    }

                    List<InsuranceParticipantModel> insuranceParticipantInsuredByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantInsuredNameByWarrantyUuid(insurancePolicy.warrantyUuid);

                    if (insuranceParticipantInsuredByWarrantyUuid.isEmpty()) {
                        insurancePolicy.insuredText = "";
                    } else {
                        int size = insuranceParticipantInsuredByWarrantyUuid.size();
                        InsuranceParticipantModel insuranceParticipantModel = insuranceParticipantInsuredByWarrantyUuid.get(0);
                        if (size == 1) {
                            insurancePolicy.insuredText = insuranceParticipantModel.name;
                        } else {
                            insurancePolicy.insuredText = insuranceParticipantModel.name + "等" + insuranceParticipantInsuredByWarrantyUuid.size() + "人";
                        }
                    }

                    lastId = policyListByWarrantyStatusOrSearch.id;
                    response.data.add(insurancePolicy);
                }
            }

            response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

            str = json(BaseResponse.CODE_SUCCESS, "获取列表成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "searchKey与warrantyStatus至少存在一个", response);
        }

        return str;
    }


    public String getInsurancePolicyDetailForOnlineStore(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyDetailForOnlineStoreRequestRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyDetailForOnlineStoreRequestRequest.class);
        InsurancePolicy.GetInsurancePolicyDetailForOnlineStoreRequestResponse response = new InsurancePolicy.GetInsurancePolicyDetailForOnlineStoreRequestResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        return getInsurancePolicyDetail(request.warrantyUuid, response);
    }


    public String getInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyListForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyListForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, insurancePolicyModel);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        Map<String, AgentBean> agentMap = new HashMap<>();
        if (StringKit.equals(request.searchType, "2")) {
            agentMap.putAll(getAgentMap(actionBean.managerUuid, request.searchKey, insurancePolicyModel));
        }

        List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = searchInsurancePolicyList(insurancePolicyModel);
        long total = searchInsurancePolicyCount(insurancePolicyModel);

        response.data = dealInsurancePolicyResultList(insurancePolicyListByWarrantyStatusOrSearch, insurancePolicyModel, agentMap);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());


        return json(BaseResponse.CODE_SUCCESS, "获取列表成功", response);
    }

    public String getInsurancePolicyListByActualPayTime(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListByActualPayTimeRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListByActualPayTimeRequest.class);
        InsurancePolicy.GetInsurancePolicyListByActualPayTimeResponse response = new InsurancePolicy.GetInsurancePolicyListByActualPayTimeResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (!StringKit.isInteger(actionBean.userId)) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        Map<String, AgentBean> agentMap = null;
        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        if (StringKit.equals(request.type, "1")) {
            insurancePolicyModel.manager_uuid = actionBean.managerUuid;
        } else if (StringKit.equals(request.type, "2")) {
            insurancePolicyModel.searchType = "2";
            AgentBean agentInfoByPersonIdManagerUuid = agentClient.getAgentInfoByPersonIdManagerUuid(actionBean.managerUuid, Long.valueOf(actionBean.userId));
            insurancePolicyModel.agent_id = String.valueOf(agentInfoByPersonIdManagerUuid.id);
            agentMap = new HashMap<>();
            agentMap.put(insurancePolicyModel.agent_id, agentInfoByPersonIdManagerUuid);
        }

        if (!StringKit.isInteger(request.startTime)) {
            insurancePolicyModel.start_time = "";
        } else {
            insurancePolicyModel.start_time = request.startTime;
        }

        if (!StringKit.isInteger(request.endTime)) {
            insurancePolicyModel.end_time = "";
        } else {
            insurancePolicyModel.end_time = request.endTime;
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<InsurancePolicyModel> insurancePolicyListByActualPayTime = insurancePolicyDao.findInsurancePolicyListByActualPayTime(insurancePolicyModel);
        long total = insurancePolicyDao.findInsurancePolicyCountByActualPayTime(insurancePolicyModel);

        response.data = dealInsurancePolicyResultList(insurancePolicyListByActualPayTime, insurancePolicyModel, agentMap);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);
    }

    public String downInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyListForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyListForManagerSystemRequest.class);
        InsurancePolicy.DownloadInsurancePolicyListForManagerSystemResponse response = new InsurancePolicy.DownloadInsurancePolicyListForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, insurancePolicyModel);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        // insurancePolicyModel.page = setPage(request.lastId, request.pageNum, "100");

        Map<String, AgentBean> agentMap = new HashMap<>();
        if (StringKit.equals(request.searchType, "2")) {
            agentMap.putAll(getAgentMap(actionBean.managerUuid, request.searchKey, insurancePolicyModel));
        }

        boolean flag = true;
        String lastId = "0";
        String pageNum = null;
        String pageSize = "100";

        String name = "";
        // 1-个人保单，2-团险保单，3-车险保单
        switch (request.warrantyType) {
            case "1":
                name = "个人";
                break;
            case "2":
                name = "团险";
                break;
            case "3":
                name = "车险";
                break;
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(name + "-保单列表");

        InsurancePolicy.GetInsurancePolicyForManagerSystem carInsurancePolicyTitle = InsurancePolicy.GetInsurancePolicyForManagerSystem.getCarInsurancePolicyTitle();
        List<ExcelModel<InsurancePolicy.GetInsurancePolicyForManagerSystem>> list = new ArrayList<>();
        ExcelModel<InsurancePolicy.GetInsurancePolicyForManagerSystem> title = new ExcelModel<>(carInsurancePolicyTitle, true, "title");
        title.boldWeight = Font.BOLDWEIGHT_BOLD;
        list.add(title);
        int startRow = 0;

        int i = ExcelModelKit.writeExcel(sheet, list, InsurancePolicy.CAR_FIELD_MAP, startRow);
        startRow += i;

        do {
            insurancePolicyModel.page = setPage(lastId, pageNum, pageSize);

            List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = searchInsurancePolicyList(insurancePolicyModel);
            List<InsurancePolicy.GetInsurancePolicyForManagerSystem> getInsurancePolicyForManagerSystems = dealInsurancePolicyResultList(insurancePolicyListByWarrantyStatusOrSearch, insurancePolicyModel, agentMap);
            list.clear();

            if (getInsurancePolicyForManagerSystems != null && !getInsurancePolicyForManagerSystems.isEmpty()) {
                for (InsurancePolicy.GetInsurancePolicyForManagerSystem getInsurancePolicyForManagerSystem : getInsurancePolicyForManagerSystems) {
                    list.add(new ExcelModel<>(getInsurancePolicyForManagerSystem));
                }

                i = ExcelModelKit.writeExcel(sheet, list, InsurancePolicy.CAR_FIELD_MAP, startRow);
                startRow += i;

                lastId = getInsurancePolicyForManagerSystems.get(getInsurancePolicyForManagerSystems.size() - 1).id;
            }

            flag = getInsurancePolicyForManagerSystems != null && !getInsurancePolicyForManagerSystems.isEmpty() && getInsurancePolicyForManagerSystems.size() >= Integer.valueOf(pageSize);

        } while (flag);


        ExcelModelKit.autoSizeColumn(sheet, InsurancePolicy.CAR_FIELD_MAP.size());

        byte[] workbookByteArray = ExcelModelKit.getWorkbookByteArray(workbook);

        if (workbookByteArray == null) {
            return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
        }

        FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
        // fileUploadRequest.base64 = Base64.getEncoder().encodeToString(workbookByteArray);
        fileUploadRequest.fileKey = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
        fileUploadRequest.fileName = fileUploadRequest.fileKey + ".xls";
        boolean upload = fileClient.upload(fileUploadRequest.fileKey, fileUploadRequest.fileName, workbookByteArray);

//        FileUploadResponse response1 = FileUpload.getInstance().uploadByBase64(fileUploadRequest);

//        if (!upload) {
//            return json(BaseResponse.CODE_FAILURE, "获取下载地址失败", response);
//        }


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

    public String checkGetInsurancePolicyListParams(InsurancePolicy.GetInsurancePolicyListForManagerSystemRequest request, InsurancePolicyModel insurancePolicyModel) {
        if (StringKit.isEmpty(request.warrantyType) || !StringKit.isNumeric(request.warrantyType) || Integer.valueOf(request.warrantyType) < 1 || Integer.valueOf(request.warrantyType) > 4) {
            return "保单类型错误";
        }
        insurancePolicyModel.type = request.warrantyType;

        if (!StringKit.isEmpty(request.warrantyStatus)) {
            switch (request.warrantyStatus) {
                case "1":
                    insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_PENDING;
                    break;
                case "2":
                    insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_WAITING;
                    break;
                case "3":
                    insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_EFFECTIVE;
                    break;
                case "4":
                    insurancePolicyModel.status_string = InsurancePolicyModel.POLICY_STATUS_INVALID + "," + InsurancePolicyModel.POLICY_STATUS_EXPIRED;
                    break;
                default:
                    return "保单状态错误";
            }
        }
        insurancePolicyModel.warranty_status = request.warrantyStatus;

        insurancePolicyModel.search = request.searchKey;
        // 1-保单号 2-代理人 3-投保人 4-车牌号
        if (!StringKit.isEmpty(request.searchType) && (!StringKit.isNumeric(request.searchType) || Integer.valueOf(request.searchType) < 1 || Integer.valueOf(request.searchType) > 4)) {
            return "搜索类型错误";
        } else if (StringKit.isEmpty(request.searchType)) {
            request.searchKey = "";
        }

        if (!StringKit.isEmpty(request.searchType) && StringKit.isEmpty(request.searchKey)) {
            return "搜索关键字";
        }

        insurancePolicyModel.searchType = request.searchType;

        if (!StringKit.isInteger(request.startTime)) {
            return "下单时间有误";
        }
        insurancePolicyModel.start_time = request.startTime;
        if (!StringKit.isInteger(request.endTime)) {
            return "下单时间有误";
        }
        insurancePolicyModel.end_time = request.endTime;

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        return null;
    }

    private List<InsurancePolicy.GetInsurancePolicyForManagerSystem> dealInsurancePolicyResultList(List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch, InsurancePolicyModel insurancePolicyModel, Map<String, AgentBean> agentMap) {
        List<InsurancePolicy.GetInsurancePolicyForManagerSystem> result = new ArrayList<>();
        if (insurancePolicyListByWarrantyStatusOrSearch == null) {
            return result;
        }

        HashMap<String, ProductBean> map = new HashMap<>();

        for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyListByWarrantyStatusOrSearch) {

            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            custWarrantyCostModel.warranty_uuid = policyListByWarrantyStatusOrSearch.warranty_uuid;

            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

            CustWarrantyCostListResult custWarrantyCostListResult = dealCustWarrantyCostList(custWarrantyCostByWarrantyUuid);

            CustWarrantyBrokerageModel custWarrantyBrokerageModel = new CustWarrantyBrokerageModel();
            custWarrantyBrokerageModel.warranty_uuid = policyListByWarrantyStatusOrSearch.warranty_uuid;

            String custWarrantyBrokerageTotalByManagerUuid = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByWarrantyUuid(custWarrantyBrokerageModel);


            InsurancePolicy.GetInsurancePolicyForManagerSystem getInsurancePolicyForManagerSystem = new InsurancePolicy.GetInsurancePolicyForManagerSystem(policyListByWarrantyStatusOrSearch, custWarrantyCostListResult.premium, custWarrantyCostListResult.payMoney, custWarrantyCostListResult.taxMoney, custWarrantyCostListResult.warrantyStatusForPay, custWarrantyCostListResult.warrantyStatusForPayText);

            getInsurancePolicyForManagerSystem.brokerage = custWarrantyBrokerageTotalByManagerUuid;
            getInsurancePolicyForManagerSystem.brokerageText = "¥" + custWarrantyBrokerageTotalByManagerUuid;

            if (StringKit.isInteger(getInsurancePolicyForManagerSystem.productId)) {

                ProductBean productBean = map.get(getInsurancePolicyForManagerSystem.productId);

                if (productBean == null) {
                    productBean = productClient.getProduct(Long.valueOf(getInsurancePolicyForManagerSystem.productId));
                }

                if (productBean != null) {
                    getInsurancePolicyForManagerSystem.productName = productBean.name;
                    getInsurancePolicyForManagerSystem.insuranceProductName = productBean.displayName;
                    getInsurancePolicyForManagerSystem.insuranceCompanyName = productBean.insuranceCoName;

                    String[] split = productBean.code.split("_");
                    if (split.length > 1) {
                        getInsurancePolicyForManagerSystem.insuranceCompanyLogo = fileClient.getFileUrl("property_key_" + split[0]);
                    }
                }
            }

            AgentBean agentBean = null;
            if (StringKit.equals(insurancePolicyModel.searchType, "2")) {
                agentBean = agentMap.get(getInsurancePolicyForManagerSystem.agentId);
            } else if (StringKit.isInteger(getInsurancePolicyForManagerSystem.agentId)) {
                agentBean = agentClient.getAgentById(Long.valueOf(getInsurancePolicyForManagerSystem.agentId));
            }

            if (agentBean != null) {
                getInsurancePolicyForManagerSystem.agentName = agentBean.name;
            }

            if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {

                if (!StringKit.isEmpty(insurancePolicyModel.search)) {
                    switch (insurancePolicyModel.searchType) {
                        case "1":
                        case "2":
                        case "4":
                            InsuranceParticipantModel insuranceParticipantPolicyHolderNameByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(policyListByWarrantyStatusOrSearch.warranty_uuid);
                            if (insuranceParticipantPolicyHolderNameByWarrantyUuid != null) {
                                getInsurancePolicyForManagerSystem.policyHolderName = insuranceParticipantPolicyHolderNameByWarrantyUuid.name;
                                getInsurancePolicyForManagerSystem.policyHolderMobile = insuranceParticipantPolicyHolderNameByWarrantyUuid.phone;
                            }
                            break;
                        case "3":
                            getInsurancePolicyForManagerSystem.policyHolderName = policyListByWarrantyStatusOrSearch.policy_holder_name;
                            getInsurancePolicyForManagerSystem.policyHolderMobile = policyListByWarrantyStatusOrSearch.policy_holder_mobile;
                            break;
                    }

                    switch (insurancePolicyModel.searchType) {
                        case "1":
                        case "2":
                        case "3":
                            CarInfoModel carInfoCarCodeAndFrameNoByWarrantyUuid = carInfoDao.findCarInfoCarCodeAndFrameNoByWarrantyUuid(policyListByWarrantyStatusOrSearch.warranty_uuid);
                            if (carInfoCarCodeAndFrameNoByWarrantyUuid != null) {
                                getInsurancePolicyForManagerSystem.frameNo = carInfoCarCodeAndFrameNoByWarrantyUuid.frame_no;
                                getInsurancePolicyForManagerSystem.carCode = carInfoCarCodeAndFrameNoByWarrantyUuid.car_code;
                            }
                            break;
                        case "4":
                            getInsurancePolicyForManagerSystem.frameNo = policyListByWarrantyStatusOrSearch.frame_no;
                            getInsurancePolicyForManagerSystem.carCode = policyListByWarrantyStatusOrSearch.car_code;
                            break;
                    }

                } else {
                    InsuranceParticipantModel insuranceParticipantPolicyHolderNameByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(policyListByWarrantyStatusOrSearch.warranty_uuid);
                    if (insuranceParticipantPolicyHolderNameByWarrantyUuid != null) {
                        getInsurancePolicyForManagerSystem.policyHolderName = insuranceParticipantPolicyHolderNameByWarrantyUuid.name;
                        getInsurancePolicyForManagerSystem.policyHolderMobile = insuranceParticipantPolicyHolderNameByWarrantyUuid.phone;
                    }

                    CarInfoModel carInfoCarCodeAndFrameNoByWarrantyUuid = carInfoDao.findCarInfoCarCodeAndFrameNoByWarrantyUuid(policyListByWarrantyStatusOrSearch.warranty_uuid);
                    if (carInfoCarCodeAndFrameNoByWarrantyUuid != null) {
                        getInsurancePolicyForManagerSystem.frameNo = carInfoCarCodeAndFrameNoByWarrantyUuid.frame_no;
                        getInsurancePolicyForManagerSystem.carCode = carInfoCarCodeAndFrameNoByWarrantyUuid.car_code;
                    }
                }

            } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_PERSON)) {

            } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_TEAM)) {

            }
            result.add(getInsurancePolicyForManagerSystem);
        }

        return result;
    }

    private Map<String, AgentBean> getAgentMap(String managerUuid, String searchKey, InsurancePolicyModel insurancePolicyModel) {
        List<AgentBean> list = agentClient.getAllBySearchName(managerUuid, searchKey);
        Map<String, AgentBean> agentMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                AgentBean agentBean = list.get(i);
                sb.append(agentBean.id);
                agentMap.put(String.valueOf(agentBean.id), agentBean);
                if (i != list.size() - 1) {
                    sb.append(",");
                }
            }
        }
        insurancePolicyModel.agent_id_string = sb.toString();
        return agentMap;
    }

    private List<InsurancePolicyModel> searchInsurancePolicyList(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyDao.findInsurancePolicyListBySearchType(insurancePolicyModel);
    }

    private long searchInsurancePolicyCount(InsurancePolicyModel insurancePolicyModel) {
        if (StringKit.isEmpty(insurancePolicyModel.warranty_status)) {
            return insurancePolicyDao.findInsurancePolicyCountBySearchType(insurancePolicyModel);
        }
        return insurancePolicyDao.findInsurancePolicyCountBySearchType(insurancePolicyModel);
    }

    public String getInsurancePolicyDetailForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyDetailForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyDetailForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyDetailForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyDetailForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        return getInsurancePolicyDetail(request.warrantyUuid, response);
    }

    public String getInsurancePolicyDetail(String warrantyUuid, InsurancePolicy.GetInsurancePolicyDetailForOnlineStoreRequestResponse response) {
        InsurancePolicyModel insurancePolicyDetailByWarrantyCode = insurancePolicyDao.findInsurancePolicyDetailByWarrantyUuid(warrantyUuid);
        String str;
        if (insurancePolicyDetailByWarrantyCode != null) {
            List<InsuranceParticipantModel> insuranceParticipantByWarrantyCode = insuranceParticipantDao.findInsuranceParticipantByWarrantyUuid(warrantyUuid);

            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            custWarrantyCostModel.warranty_uuid = insurancePolicyDetailByWarrantyCode.warranty_uuid;

            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

            CustWarrantyCostListResult custWarrantyCostListResult = dealCustWarrantyCostList(custWarrantyCostByWarrantyUuid);

            response.data = new InsurancePolicy.GetInsurancePolicyDetail(insurancePolicyDetailByWarrantyCode, custWarrantyCostListResult.premium, custWarrantyCostListResult.payMoney, custWarrantyCostListResult.taxMoney, custWarrantyCostListResult.warrantyStatusForPay, custWarrantyCostListResult.warrantyStatusForPayText);

            response.data.insuredList = new ArrayList<>();
            response.data.beneficiaryList = new ArrayList<>();

            ProductBean product = null;
            if (StringKit.isInteger(insurancePolicyDetailByWarrantyCode.product_id)) {
                product = productClient.getProduct(Long.valueOf(insurancePolicyDetailByWarrantyCode.product_id));
            }

            if (insuranceParticipantByWarrantyCode != null && !insuranceParticipantByWarrantyCode.isEmpty()) {
                if (product != null) {
                    response.data.insuranceProductName = product.displayName;
                    response.data.insuranceCompanyName = product.insuranceCoName;

                    String[] split = product.code.split("_");
                    if (split.length > 1) {
                        response.data.insuranceCompanyLogo = fileClient.getFileUrl("property_key_" + split[0]);
                    }
                }

                for (InsuranceParticipantModel insuranceParticipantModel : insuranceParticipantByWarrantyCode) {
                    InsurancePolicy.InsurancePolicyParticipantInfo insurancePolicyParticipantInfo = new InsurancePolicy.InsurancePolicyParticipantInfo(insuranceParticipantModel);
                    if (StringKit.equals(insurancePolicyParticipantInfo.type, InsuranceParticipantModel.TYPE_POLICYHOLDER)) {
                        response.data.policyHolder = insurancePolicyParticipantInfo;
                    } else if (StringKit.equals(insurancePolicyParticipantInfo.type, InsuranceParticipantModel.TYPE_INSURED)) {
                        response.data.insuredList.add(insurancePolicyParticipantInfo);
                    } else if (StringKit.equals(insurancePolicyParticipantInfo.type, InsuranceParticipantModel.TYPE_BENEFICIARY)) {
                        response.data.beneficiaryList.add(insurancePolicyParticipantInfo);
                    }
                }
            }

            if (StringKit.equals(insurancePolicyDetailByWarrantyCode.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                // 车险
                CarInfoModel oneByWarrantyCode = carInfoDao.findOneByWarrantyUuid(warrantyUuid);
                response.data.carInfo = new InsurancePolicy.CarInfo(oneByWarrantyCode);
                response.data.bizId = oneByWarrantyCode.biz_id;
                response.data.bjCodeFlag = oneByWarrantyCode.bj_code_flag;
                str = json(BaseResponse.CODE_SUCCESS, "获取保单详情成功", response);
            } else {
                // 其他险
                str = json(BaseResponse.CODE_SUCCESS, "获取保单详情成功", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "数据不存在", response);
        }

        return str;
    }

    public String getInsurancePolicyPremiumStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        response.data = new InsurancePolicy.InsurancePolicyStatistic();

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();

        Calendar instance = Calendar.getInstance();

        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);

        custWarrantyCostModel.manager_uuid = actionBean.managerUuid;

        //noinspection MagicConstant
        instance.set(year, month, day, 0, 0, 0);
        custWarrantyCostModel.start_time = String.valueOf(instance.getTimeInMillis());

        custWarrantyCostModel.end_time = String.valueOf(getDayEndTime(year, day, month));

        // 当天的所有付款的
        String dayAmount = custWarrantyCostDao.findCustWarrantyCostTotalByManagerUuid(custWarrantyCostModel);

        //noinspection MagicConstant
        instance.set(year, month, 1, 0, 0, 0);
        custWarrantyCostModel.start_time = String.valueOf(instance.getTimeInMillis());

        custWarrantyCostModel.end_time = String.valueOf(getMonthEndTime(year, month));

        // 当月的所有付款的
        String monthAmount = custWarrantyCostDao.findCustWarrantyCostTotalByManagerUuid(custWarrantyCostModel);

        custWarrantyCostModel.start_time = "";
        custWarrantyCostModel.end_time = "";

        String totalAmount = custWarrantyCostDao.findCustWarrantyCostTotalByManagerUuid(custWarrantyCostModel);

        response.data.dayAmount = "本日保费\n" + dayAmount;
        response.data.monthAmount = "本月保费\n" + monthAmount;
        response.data.totalAmount = "累计保费\n" + totalAmount;

        return json(BaseResponse.CODE_SUCCESS, "获取保单统计成功", response);
    }

    public String getInsurancePolicyBrokerageStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        response.data = new InsurancePolicy.InsurancePolicyStatistic();

        CustWarrantyBrokerageModel custWarrantyBrokerageModel = new CustWarrantyBrokerageModel();

        if (!StringKit.isInteger(request.startTime)) {
            custWarrantyBrokerageModel.start_time = "";
        } else {
            custWarrantyBrokerageModel.start_time = request.startTime;
        }

        if (!StringKit.isInteger(request.endTime)) {
            custWarrantyBrokerageModel.end_time = "";
        } else {
            custWarrantyBrokerageModel.end_time = request.endTime;
        }

        custWarrantyBrokerageModel.channel_id = request.channelId;

        custWarrantyBrokerageModel.manager_uuid = actionBean.managerUuid;

        Calendar instance = Calendar.getInstance();

        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);

        InsurancePolicyModel insurancePolicyModel1 = new InsurancePolicyModel();
        custWarrantyBrokerageModel.manager_uuid = actionBean.managerUuid;

        //noinspection MagicConstant
        instance.set(year, month, day, 0, 0, 0);
        insurancePolicyModel1.start_time = String.valueOf(instance.getTimeInMillis());
        insurancePolicyModel1.end_time = String.valueOf(getDayEndTime(year, month, day));

        // 当天的所有付款的
        String dayAmount = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByManagerUuid(custWarrantyBrokerageModel);

        //noinspection MagicConstant
        instance.set(year, month, 1, 0, 0, 0);
        insurancePolicyModel1.start_time = String.valueOf(instance.getTimeInMillis());
        insurancePolicyModel1.end_time = String.valueOf(getMonthEndTime(year, month));

        // 当月的所有付款的
        String monthAmount = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByManagerUuid(custWarrantyBrokerageModel);

        insurancePolicyModel1.start_time = "";
        insurancePolicyModel1.end_time = "";

        String totalAmount = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByManagerUuid(custWarrantyBrokerageModel);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        response.data.dayAmount = "本日佣金\n" + dayAmount;
        response.data.monthAmount = "本月佣金\n" + monthAmount;
        response.data.totalAmount = "累计佣金\n" + totalAmount;

        return json(BaseResponse.CODE_SUCCESS, "获取佣金统计成功", response);
    }

    public String getInsurancePolicyStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyStatisticDetailForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        CustWarrantyBrokerageModel custWarrantyBrokerageModel = new CustWarrantyBrokerageModel();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String startTime;
        String endTime;

        // 时间范围类型，1-今日，2-本月，3-本年
        switch (request.timeRangeType) {
            case "1":
                calendar.set(year, month, day, 0, 0, 0);
                startTime = String.valueOf(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            case "2":
                calendar.set(year, month, 1, 0, 0, 0);
                startTime = String.valueOf(calendar.getTimeInMillis());
                calendar.add(Calendar.MONTH, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            case "3":
                calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                startTime = String.valueOf(calendar.getTimeInMillis());
                calendar.add(Calendar.YEAR, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            case "4":
                startTime = "0";
                calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                calendar.add(Calendar.YEAR, 1);
                endTime = String.valueOf(calendar.getTimeInMillis());
                break;
            default:
                return json(BaseResponse.CODE_FAILURE, "时间范围类型错误", response);
        }
        custWarrantyCostModel.start_time = startTime;
        custWarrantyCostModel.end_time = endTime;
        custWarrantyCostModel.time_range_type = request.timeRangeType;
        custWarrantyCostModel.manager_uuid = actionBean.managerUuid;

        custWarrantyBrokerageModel.start_time = startTime;
        custWarrantyBrokerageModel.end_time = endTime;
        custWarrantyBrokerageModel.time_range_type = request.timeRangeType;
        custWarrantyBrokerageModel.manager_uuid = actionBean.managerUuid;

        List<PremiumStatisticModel> custWarrantyCostStatistic = custWarrantyCostDao.findCustWarrantyCostStatistic(custWarrantyCostModel);
        List<BrokerageStatisticModel> custWarrantyBrokerageStatistic = custWarrantyBrokerageDao.findCustWarrantyBrokerageStatistic(custWarrantyBrokerageModel);

        LinkedHashMap<String, InsurancePolicy.InsurancePolicyStatisticItem> map = new LinkedHashMap<>();

        response.data = new InsurancePolicy.InsurancePolicyStatisticDetail();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        response.data.startTime = startTime;
        response.data.startTimeText = sdf.format(new Date(Long.valueOf(startTime)));
        response.data.endTime = endTime;
        response.data.endTimeText = sdf.format(new Date(Long.valueOf(endTime)));

        BigDecimal premium = new BigDecimal("0.00");
        int count = 0;
        if (custWarrantyCostStatistic != null && !custWarrantyCostStatistic.isEmpty()) {
            for (PremiumStatisticModel premiumStatisticModel : custWarrantyCostStatistic) {
                InsurancePolicy.InsurancePolicyStatisticItem item = new InsurancePolicy.InsurancePolicyStatisticItem(premiumStatisticModel.time_text);
                item.setPremiumStatisticModel(premiumStatisticModel);
                map.put(premiumStatisticModel.time_text, item);
                premium = premium.add(new BigDecimal(premiumStatisticModel.premium));

                if (StringKit.isInteger(premiumStatisticModel.insurance_policy_count)) {
                    count += Integer.valueOf(premiumStatisticModel.insurance_policy_count);
                }
            }
        }

        BigDecimal brokerage = new BigDecimal("0.00");
        if (custWarrantyBrokerageStatistic != null && !custWarrantyBrokerageStatistic.isEmpty()) {
            for (BrokerageStatisticModel brokerageStatisticModel : custWarrantyBrokerageStatistic) {
                InsurancePolicy.InsurancePolicyStatisticItem item = map.get(brokerageStatisticModel.time_text);
                if (item == null) {
                    item = new InsurancePolicy.InsurancePolicyStatisticItem(brokerageStatisticModel.time_text);
                    map.put(brokerageStatisticModel.time_text, item);
                }
                item.setBrokerageStatisticModel(brokerageStatisticModel);
                brokerage = brokerage.add(new BigDecimal(brokerageStatisticModel.brokerage));
            }
        }

        if (map.isEmpty()) {
            response.data.insurancePolicyCount = "0";
            response.data.premium = "0.00";
            response.data.premiumText = "¥0.00";

            response.data.brokerage = "0.00";
            response.data.brokerageText = "¥0.00";
            response.data.brokeragePercentage = "0.0";
            response.data.brokeragePercentageText = "0.00%";

            response.data.averagePremium = "0.00";
            response.data.averagePremiumText = "¥0.00";

        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            DecimalFormat moneyFormat = new DecimalFormat("###,###,###,###,##0.00");

            response.data.insurancePolicyCount = String.valueOf(count);
            response.data.premium = decimalFormat.format(premium.doubleValue());
            response.data.premiumText = "¥" + moneyFormat.format(new BigDecimal(response.data.premium));

            response.data.brokerage = decimalFormat.format(brokerage.doubleValue());
            response.data.brokerageText = "¥" + moneyFormat.format(new BigDecimal(response.data.brokerage));

            if (premium.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal divide = brokerage.divide(premium, BigDecimal.ROUND_HALF_DOWN);
                response.data.brokeragePercentage = String.valueOf(divide.doubleValue());
                divide = divide.multiply(new BigDecimal("100"));
                response.data.brokeragePercentageText = decimalFormat.format(divide.doubleValue()) + "%";
            } else {
                response.data.brokeragePercentage = "0.0";
                response.data.brokeragePercentageText = "0.00%";
            }

            BigDecimal bigDecimal = new BigDecimal(response.data.insurancePolicyCount);
            if (map.size() != 0 && bigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal divide = premium.divide(bigDecimal, BigDecimal.ROUND_HALF_DOWN);
                response.data.averagePremium = decimalFormat.format(divide.doubleValue());
                response.data.averagePremiumText = "¥" + moneyFormat.format(new BigDecimal(response.data.averagePremium));
            } else {
                response.data.averagePremium = "0.00";
                response.data.averagePremiumText = "¥0.00";
            }

        }

        response.data.insurancePolicyList = dealPercentageByList(map, premium, brokerage);

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);
    }

    private List<InsurancePolicy.InsurancePolicyStatisticItem> dealPercentageByList(LinkedHashMap<String, InsurancePolicy.InsurancePolicyStatisticItem> map, BigDecimal premium, BigDecimal brokerage) {
        List<InsurancePolicy.InsurancePolicyStatisticItem> result = new ArrayList<>();
        Set<String> strings = map.keySet();
        if (!strings.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            DecimalFormat moneyFormat = new DecimalFormat("###,###,###,###,##0.00");
            for (String string : strings) {
                InsurancePolicy.InsurancePolicyStatisticItem item = map.get(string);

                BigDecimal itemPremium;
                if (StringKit.isEmpty(item.premium) && !StringKit.isNumeric(item.premium)) {
                    itemPremium = new BigDecimal("0.00");
                    item.premium = "0.00";
                    item.premiumText = "¥0.00";
                    item.averagePremium = "0.00";
                    item.averagePremiumText = "¥0.00";
                    item.premiumPercentage = "0.0";
                    item.premiumPercentageText = "0.00%";
                } else {
                    itemPremium = new BigDecimal(item.premium);
                }

                BigDecimal itemBrokerage;
                if (StringKit.isEmpty(item.brokerage) && !StringKit.isNumeric(item.brokerage)) {
                    itemBrokerage = new BigDecimal("0.00");
                    item.brokerage = "0.00";
                    item.brokerageText = "¥0.00";
                    item.averageBrokeragePercentage = "0.0";
                    item.averageBrokeragePercentageText = "0.00%";
                } else {
                    itemBrokerage = new BigDecimal(item.brokerage);
                }

                BigDecimal insurancePolicyCount;
                if (StringKit.isEmpty(item.insurancePolicyCount) && !StringKit.isInteger(item.insurancePolicyCount)) {
                    insurancePolicyCount = new BigDecimal("0");
                    item.insurancePolicyCount = "0";
                } else {
                    insurancePolicyCount = new BigDecimal(item.insurancePolicyCount);
                }

                if (itemPremium.compareTo(BigDecimal.ZERO) != 0) {
                    if (!StringKit.equals(item.insurancePolicyCount, "0")) {
                        BigDecimal divide = itemPremium.divide(insurancePolicyCount, BigDecimal.ROUND_HALF_DOWN);
                        item.averagePremium = moneyFormat.format(divide.doubleValue());
                        item.averagePremiumText = "¥" + item.averagePremium;
                    } else {
                        item.averagePremium = "0.00";
                        item.averagePremiumText = "¥0.00";
                    }

                    if (premium.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal divide = itemPremium.divide(premium, BigDecimal.ROUND_HALF_DOWN);
                        item.premiumPercentage = String.valueOf((divide.doubleValue()));
                        divide = divide.multiply(new BigDecimal("100"));
                        item.premiumPercentageText = decimalFormat.format(divide.doubleValue()) + "%";
                        ;
                    } else {
                        item.premiumPercentage = "0.0";
                        item.premiumPercentageText = "0.00%";
                    }
                } else {
                    item.premium = "0.00";
                    item.premiumText = "¥0.00";
                    item.averagePremium = "0.00";
                    item.averagePremiumText = "¥0.00";
                    item.premiumPercentage = "0.0";
                    item.premiumPercentageText = "0.00%";
                }

                if (itemPremium.compareTo(BigDecimal.ZERO) != 0 && itemBrokerage.compareTo(BigDecimal.ZERO) != 0 && insurancePolicyCount.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal divide = itemBrokerage.divide(itemPremium, BigDecimal.ROUND_HALF_DOWN).divide(insurancePolicyCount, BigDecimal.ROUND_HALF_DOWN);
                    item.averageBrokeragePercentage = String.valueOf((divide.doubleValue()));
                    divide = divide.multiply(new BigDecimal("100"));
                    item.averageBrokeragePercentageText = decimalFormat.format(divide.doubleValue()) + "%";
                } else {
                    item.averageBrokeragePercentage = "0.0";
                    item.averageBrokeragePercentageText = "0.00%";
                }

                item.premium = decimalFormat.format(itemPremium.doubleValue());
                item.premiumText = "¥" + moneyFormat.format(new BigDecimal(item.premium));
                item.brokerage = decimalFormat.format(itemBrokerage.doubleValue());
                item.brokerageText = "¥" + moneyFormat.format(new BigDecimal(item.brokerage));

                result.add(item);
            }
        }

        return result;
    }

    public String getInsurancePolicyBrokerageStatisticListForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyBrokerageStatisticListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyBrokerageStatisticListRequest.class);
        InsurancePolicy.GetInsurancePolicyBrokerageStatisticListResponse response = new InsurancePolicy.GetInsurancePolicyBrokerageStatisticListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
        custWarrantyCostModel.search = request.searchKey;

        if (StringKit.isEmpty(request.startTime)) {
            long l = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
            custWarrantyCostModel.start_time = String.valueOf(l);
        } else {
            custWarrantyCostModel.start_time = request.startTime;
        }

        if (StringKit.isEmpty(request.endTime)) {
            custWarrantyCostModel.end_time = String.valueOf(System.currentTimeMillis());
        } else {
            custWarrantyCostModel.end_time = request.endTime;
        }

        custWarrantyCostModel.manager_uuid = actionBean.managerUuid;

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "20";
        }
        custWarrantyCostModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<BrokerageStatisticListModel> insurancePolicyBrokerageStatisticList = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticList(custWarrantyCostModel);
        long total = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticListCount(custWarrantyCostModel);
        response.data = new ArrayList<>();
        String lastId = "0";

        if (insurancePolicyBrokerageStatisticList != null && !insurancePolicyBrokerageStatisticList.isEmpty()) {
            for (BrokerageStatisticListModel brokerageStatisticListModel : insurancePolicyBrokerageStatisticList) {
                InsuranceParticipantModel holder = insuranceParticipantDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(brokerageStatisticListModel.warranty_uuid);
                InsurancePolicy.InsurancePolicyBrokerageStatistic insurancePolicyBrokerageStatistic = new InsurancePolicy.InsurancePolicyBrokerageStatistic(brokerageStatisticListModel);
                insurancePolicyBrokerageStatistic.customerName = holder.name;
                insurancePolicyBrokerageStatistic.customerMobile = holder.phone;
                if (StringKit.isInteger(brokerageStatisticListModel.product_id)) {
                    ProductBean product = productClient.getProduct(Long.valueOf(brokerageStatisticListModel.product_id));
                    if (product != null) {
                        insurancePolicyBrokerageStatistic.insuranceName = product.insuranceCoName;
                        insurancePolicyBrokerageStatistic.productName = product.displayName;
                    }
                }
                lastId = insurancePolicyBrokerageStatistic.costId;
                response.data.add(insurancePolicyBrokerageStatistic);
            }
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);

    }

    public String offlineInsurancePolicyInput(ActionBean actionBean) {
        InsurancePolicy.OfflineInsurancePolicyInputRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.OfflineInsurancePolicyInputRequest.class);
        InsurancePolicy.OfflineInsurancePolicyInputResponse response = new InsurancePolicy.OfflineInsurancePolicyInputResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        String fileUrl = fileClient.getFileUrl(request.fileKey);

        if (StringKit.isEmpty(fileUrl)) {
            return json(BaseResponse.CODE_FAILURE, "无法获取报表文件", response);
        }

        InputStream inputStream = HttpClientKit.downloadFile(fileUrl);

        if (inputStream == null) {
            return json(BaseResponse.CODE_FAILURE, "获取报表文件失败", response);
        }


        response.data = new InsurancePolicy.OfflineInsurancePolicyDetail();
        response.data.list = new ArrayList<>();

        boolean flag = false;

        Workbook wb = null;
        List<OfflineInsurancePolicyModel> errorList = new ArrayList<>();
        try {
            wb = WorkbookFactory.create(inputStream);
            String time = String.valueOf(System.currentTimeMillis());

            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    OfflineInsurancePolicyModel offlineInsurancePolicyModel = ExcelModelKit.createModel(OfflineInsurancePolicyModel.class, OfflineInsurancePolicyModel.COLUMN_FIELD_MAP, row);

                    if (offlineInsurancePolicyModel == null) {
                        continue;
                    }

                    if (offlineInsurancePolicyModel.isEmptyLine()) {
                        continue;
                    }

                    if (offlineInsurancePolicyModel.isTitle()) {
                        continue;
                    }

                    if (!offlineInsurancePolicyModel.isEnable()) {
                        offlineInsurancePolicyModel.reason = "缺少必填字段";
                        response.data.list.add(new InsurancePolicy.OfflineInsurancePolicy(offlineInsurancePolicyModel));
                        errorList.add(offlineInsurancePolicyModel);
                        continue;
                    }

                    OfflineInsurancePolicyModel offlineInsurance = offlineInsurancePolicyDao.findOfflineInsurancePolicyByWarrantyCode(offlineInsurancePolicyModel.warranty_code);

                    if (offlineInsurance != null) {
                        offlineInsurancePolicyModel.reason = "保单号重复";
                        response.data.list.add(new InsurancePolicy.OfflineInsurancePolicy(offlineInsurancePolicyModel));
                        errorList.add(offlineInsurancePolicyModel);
                    } else {

                        offlineInsurancePolicyModel.manager_uuid = actionBean.managerUuid;
                        offlineInsurancePolicyModel.warranty_uuid = String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());
                        offlineInsurancePolicyModel.created_at = time;
                        offlineInsurancePolicyModel.updated_at = time;
                        offlineInsurancePolicyModel.state = "1";

                        long l = offlineInsurancePolicyDao.addOfflineInsurancePolicy(offlineInsurancePolicyModel);
                        if (l <= 0) {
                            offlineInsurancePolicyModel.reason = "添加数据失败";
                            response.data.list.add(new InsurancePolicy.OfflineInsurancePolicy(offlineInsurancePolicyModel));
                            errorList.add(offlineInsurancePolicyModel);
                        }
                    }
                }
            }

            flag = response.data.list == null || response.data.list.isEmpty();

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!flag && response.data.list != null && !response.data.list.isEmpty()) {
            OfflineInsurancePolicyModel titleModel = OfflineInsurancePolicyModel.getTitleModel();
            errorList.add(0, titleModel);

            List<ExcelModel<OfflineInsurancePolicyModel>> dataList = new ArrayList<>();
            for (OfflineInsurancePolicyModel offlineInsurancePolicyModel : errorList) {
                dataList.add(new ExcelModel<>(offlineInsurancePolicyModel));
            }

            byte[] data = ExcelModelKit.createExcelByteArray(dataList, OfflineInsurancePolicyModel.COLUMN_FIELD_MAP, "导入失败保单数据");

            if (data == null) {
                return json(BaseResponse.CODE_FAILURE, "导入失败", response);
            }

            FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
            // fileUploadRequest.base64 = Base64.getEncoder().encodeToString(data);
            fileUploadRequest.fileKey = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
            fileUploadRequest.fileName = fileUploadRequest.fileKey + ".xls";

            boolean upload = fileClient.upload(fileUploadRequest.fileKey, fileUploadRequest.fileName, data);
            // FileUploadResponse response1 = FileUpload.getInstance().uploadByBase64(fileUploadRequest);

            if (upload) {
                response.data.excelFileKey = fileUploadRequest.fileKey;
                response.data.excelFileUrl = fileClient.getFileUrl(fileUploadRequest.fileKey);
                return json(BaseResponse.CODE_FAILURE, "部分导入失败", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "导入失败", response);
            }

        }

        return json((flag ? BaseResponse.CODE_SUCCESS : BaseResponse.CODE_FAILURE), (flag ? "导入成功" : "部分导入失败"), response);
    }

    private long getDayEndTime(int year, int month, int day) {
        Calendar instance = Calendar.getInstance();

        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.MONTH, month);
        instance.set(Calendar.DAY_OF_MONTH, day);

        int actualMaximum = instance.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (day < actualMaximum) {
            instance.set(year, month, day + 1, 0, 0, 0);
        } else {
            if (month + 1 < 12) {
                //noinspection MagicConstant
                instance.set(year, month + 1, 1, 0, 0, 0);
            } else {
                instance.set(year + 1, 0, 1, 0, 0, 0);
            }
        }

        return instance.getTimeInMillis();
    }

    private long getMonthEndTime(int year, int month) {
        Calendar instance = Calendar.getInstance();

        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.MONTH, month);
        instance.set(Calendar.DAY_OF_MONTH, 1);

        if (month + 1 < 12) {
            //noinspection MagicConstant
            instance.set(year, month + 1, 1, 0, 0, 0);
        } else {
            instance.set(year + 1, 0, 1, 0, 0, 0);
        }

        return instance.getTimeInMillis();
    }

    static class CustWarrantyCostListResult {
        BigDecimal premium = new BigDecimal("0.00");
        BigDecimal payMoney = new BigDecimal("0.00");
        BigDecimal taxMoney = new BigDecimal("0.00");
        String warrantyStatusForPay = "";
        String warrantyStatusForPayText = "";
    }

    private CustWarrantyCostListResult dealCustWarrantyCostList(List<CustWarrantyCostModel> list) {
        CustWarrantyCostListResult custWarrantyCostListResult = new CustWarrantyCostListResult();

        boolean isFindLast = false;
        int size1 = list.size();

        for (int i = size1 - 1; i > -1; i--) {
            CustWarrantyCostModel custWarrantyCostModel1 = list.get(i);
            if (StringKit.isNumeric(custWarrantyCostModel1.premium)) {
                custWarrantyCostListResult.premium = custWarrantyCostListResult.premium.add(new BigDecimal(custWarrantyCostModel1.premium));
            }

            if (StringKit.isNumeric(custWarrantyCostModel1.tax_money)) {
                custWarrantyCostListResult.taxMoney = custWarrantyCostListResult.taxMoney.add(new BigDecimal(custWarrantyCostModel1.tax_money));
            }

            if (StringKit.isNumeric(custWarrantyCostModel1.pay_money)) {
                custWarrantyCostListResult.payMoney = custWarrantyCostListResult.payMoney.add(new BigDecimal(custWarrantyCostModel1.pay_money));
            }

            if (!isFindLast) {
                isFindLast = !StringKit.isEmpty(custWarrantyCostModel1.actual_pay_time);
            }

            if (isFindLast || i == 0) {
                custWarrantyCostListResult.warrantyStatusForPay = custWarrantyCostModel1.pay_status;
                custWarrantyCostListResult.warrantyStatusForPayText = custWarrantyCostModel1.payStatusText(custWarrantyCostModel1.pay_status);
            }
        }

        return custWarrantyCostListResult;
    }

}