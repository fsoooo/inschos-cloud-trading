package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicyBean;
import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.client.*;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.ExcelModel;
import com.inschos.cloud.trading.assist.kit.ExcelModelKit;
import com.inschos.cloud.trading.assist.kit.WarrantyUuidWorker;
import com.inschos.cloud.trading.data.dao.*;
import com.inschos.cloud.trading.extend.file.FileUpload;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
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
    private CustWarrantyDao custWarrantyDao;

    @Autowired
    private CarInfoDao carInfoDao;

    @Autowired
    private CustWarrantyPersonDao custWarrantyPersonDao;

    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;

    @Autowired
    private OfflineCustWarrantyDao offlineCustWarrantyDao;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CompanyClient companyClient;

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private ChannelClient channelClient;

    @Autowired
    private FileClient fileClient;

    /**
     * FINISH: 2018/6/8
     * 获取保单列表（商朝）
     * {@link #checkGetInsurancePolicyListParams} 检查获取列表参数
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表json
     */
    public String getInsurancePolicyListForOnlineStore(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyListRequest.class);
        InsurancePolicyBean.GetInsurancePolicyListResponse response = new InsurancePolicyBean.GetInsurancePolicyListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarranty custWarranty = new CustWarranty();

        custWarranty.manager_uuid = actionBean.managerUuid;
        if ("1".equals(request.queryWay)) {
            if (actionBean.userType == 4) {
                AgentBean agentBean = agentClient.getAgentInfoByPersonIdManagerUuid(actionBean.managerUuid, Long.valueOf(actionBean.userId));
                if (agentBean != null) {
                    custWarranty.agent_id = String.valueOf(agentBean.id);
                }
            }
        } else {
            custWarranty.account_uuid = actionBean.accountUuid;
        }

        if (actionBean.managerUuid.equals("14463303497682968") && "3".equals(request.searchType)) {
            request.searchKey = "";
        }

        String s = checkGetInsurancePolicyListParams(request, custWarranty);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        List<CustWarranty> insurancePolicyListForOnlineStore = custWarrantyDao.findInsurancePolicyListForOnlineStore(custWarranty);
        long total = custWarrantyDao.findInsurancePolicyCountForOnlineStore(custWarranty);

        DealInsurancePolicyResultParameter parameter = new DealInsurancePolicyResultParameter();

        parameter.needLogo = true;
        parameter.needInsured = true;
        parameter.needChannel = false;
        parameter.needAgent = false;
        parameter.needSettlement = false;

        response.data = dealInsurancePolicyResultList(insurancePolicyListForOnlineStore, parameter);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());


        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);

    }

    /**
     * FINISH: 2018/6/8
     * 获取保单列表（业管）
     * {@link #checkGetInsurancePolicyListParams} 检查获取列表参数
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表json
     */
    public String getInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyListRequest.class);
        InsurancePolicyBean.GetInsurancePolicyListResponse response = new InsurancePolicyBean.GetInsurancePolicyListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarranty custWarranty = new CustWarranty();
        custWarranty.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, custWarranty);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        List<CustWarranty> insurancePolicyListByWarrantyStatusOrSearch = custWarrantyDao.findInsurancePolicyListForManagerSystem(custWarranty);
        long total = custWarrantyDao.findInsurancePolicyCountForManagerSystem(custWarranty);

        DealInsurancePolicyResultParameter parameter = new DealInsurancePolicyResultParameter();

        parameter.needLogo = false;
        parameter.needInsured = false;
        parameter.needChannel = false;
        parameter.needAgent = false;
        parameter.needSettlement = true;

        response.data = dealInsurancePolicyResultList(insurancePolicyListByWarrantyStatusOrSearch, parameter);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 导出保单列表（业管）
     * {@link #checkGetInsurancePolicyListParams} 检查获取列表参数
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表Excel文件地址json
     */
    public String downInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyListRequest.class);
        InsurancePolicyBean.DownloadInsurancePolicyListForManagerSystemResponse response = new InsurancePolicyBean.DownloadInsurancePolicyListForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarranty custWarranty = new CustWarranty();
        custWarranty.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, custWarranty);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        String lastId = "0";
        String pageSize = "1000";
        String name = "";
        // 1-个人保单，2-团险保单，3-车险保单
        switch (request.warrantyType) {
            case "1":
                name = "个人-";
                break;
            case "2":
                name = "团险-";
                break;
            case "3":
                name = "车险-";
                break;
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(name + "保单列表");

        InsurancePolicyBean.GetInsurancePolicyItemBean carInsurancePolicyTitle = InsurancePolicyBean.GetInsurancePolicyItemBean.getCarInsurancePolicyTitle();
        List<ExcelModel<InsurancePolicyBean.GetInsurancePolicyItemBean>> list = new ArrayList<>();
        ExcelModel<InsurancePolicyBean.GetInsurancePolicyItemBean> title = new ExcelModel<>(carInsurancePolicyTitle, true, "title");
        title.boldWeight = Font.BOLDWEIGHT_BOLD;
        list.add(title);
        int startRow = 0;

        Map<String, String> columnFieldMap = ExcelModelKit.getColumnFieldMap(InsurancePolicyBean.CAR_FIELD_LIST, 0);

        Map<String, CellStyle> cellStyleMap = ExcelModelKit.getCellStyleMap();
//        int i = ExcelModelKit.writeRank(sheet, list, InsurancePolicy.CAR_FIELD_MAP, startRow, cellStyleMap);
        int i = ExcelModelKit.writeRank(sheet, list, columnFieldMap, startRow, cellStyleMap, new HashMap<>());
        startRow += i;
        boolean flag;

        custWarranty.needBrokerage = "1";

        do {
            custWarranty.page = setPage(lastId, null, pageSize);

            List<CustWarranty> insurancePolicyListByWarrantyStatusOrSearch = custWarrantyDao.findInsurancePolicyListForManagerSystem(custWarranty);

            DealInsurancePolicyResultParameter parameter = new DealInsurancePolicyResultParameter();

            parameter.needLogo = false;
            parameter.needInsured = false;
            parameter.needChannel = true;
            parameter.needAgent = true;
            parameter.needSettlement = true;

            List<InsurancePolicyBean.GetInsurancePolicyItemBean> getInsurancePolicyItemBeans = dealInsurancePolicyResultList(insurancePolicyListByWarrantyStatusOrSearch, parameter);
            list.clear();

            if (getInsurancePolicyItemBeans != null && !getInsurancePolicyItemBeans.isEmpty()) {
                for (InsurancePolicyBean.GetInsurancePolicyItemBean getInsurancePolicyItemBean : getInsurancePolicyItemBeans) {
                    list.add(new ExcelModel<>(getInsurancePolicyItemBean));
                }

//                i = ExcelModelKit.writeRank(sheet, list, InsurancePolicy.CAR_FIELD_MAP, startRow, cellStyleMap);
                i = ExcelModelKit.writeRank(sheet, list, columnFieldMap, startRow, cellStyleMap, InsurancePolicyBean.CAR_FIELD_TYPE);
                startRow += i;

                lastId = getInsurancePolicyItemBeans.get(getInsurancePolicyItemBeans.size() - 1).id;
            }

            flag = getInsurancePolicyItemBeans != null && !getInsurancePolicyItemBeans.isEmpty() && getInsurancePolicyItemBeans.size() >= Integer.valueOf(pageSize);

        } while (flag);

        ExcelModelKit.autoSizeColumn(sheet, columnFieldMap.size());

        byte[] workbookByteArray = ExcelModelKit.getWorkbookByteArray(workbook);

        if (workbookByteArray == null) {
            return json(BaseResponse.CODE_FAILURE, "生成表格失败", response);
        }

        FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
        // fileUploadRequest.base64 = Base64.getEncoder().encodeToString(workbookByteArray);
        fileUploadRequest.fileKey = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
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

    /**
     * FINISH: 2018/6/8
     * 获取下载数据的数据量大小
     *
     * @param actionBean 请求bean
     * @return 下载数据的数据量json
     */
    public String getDownInsurancePolicyCountForManagerSystem(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyListRequest.class);
        InsurancePolicyBean.GetDownInsurancePolicyCountForManagerSystem response = new InsurancePolicyBean.GetDownInsurancePolicyCountForManagerSystem();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarranty custWarranty = new CustWarranty();
        custWarranty.manager_uuid = actionBean.managerUuid;

        String s = checkGetInsurancePolicyListParams(request, custWarranty);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        long total = custWarrantyDao.findInsurancePolicyCountForManagerSystem(custWarranty);

        custWarranty.page = setPage(null, "1", "5");
        custWarranty.needBrokerage = "1";

        response.data = new InsurancePolicyBean.DownInsurancePolicy();

        response.data.count = "一共 " + String.valueOf(total) + " 条数据";
        long l = (total / 10) * 5;
        response.data.time = l + "秒";

        DealInsurancePolicyResultParameter parameter = new DealInsurancePolicyResultParameter();

        parameter.needLogo = false;
        parameter.needInsured = true;
        parameter.needChannel = true;
        parameter.needAgent = true;
        parameter.needSettlement = true;

        response.data.list = dealInsurancePolicyResultList(custWarrantyDao.findInsurancePolicyListForManagerSystem(custWarranty), parameter);

        return json(BaseResponse.CODE_SUCCESS, "获取数据量大小成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 保单详情（业管）
     * {@link #getInsurancePolicyDetail(String warrantyUuid, InsurancePolicyBean.GetInsurancePolicyDetailResponse response)} 获取保单详情
     *
     * @param actionBean 请求bean
     * @return 保单详情json
     */
    public String getInsurancePolicyDetail(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyDetailRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyDetailRequest.class);
        InsurancePolicyBean.GetInsurancePolicyDetailResponse response = new InsurancePolicyBean.GetInsurancePolicyDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        return getInsurancePolicyDetail(request.warrantyUuid, response);
    }

    /**
     * FINISH: 2018/6/8
     * 保单统计
     * {@link #dealPercentageByList} 处理统计数据
     *
     * @param actionBean 请求bean
     * @return 保单统计json
     */
    public String getInsurancePolicyStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyStatisticDetailForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyStatisticDetailForManagerSystemRequest.class);
        InsurancePolicyBean.GetInsurancePolicyStatisticDetailForManagerSystemResponse response = new InsurancePolicyBean.GetInsurancePolicyStatisticDetailForManagerSystemResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
        CustWarrantyBrokerage custWarrantyBrokerage = new CustWarrantyBrokerage();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long startTime;
        long endTime;

        // 时间范围类型，1-今日，2-本月，3-本年，4-历年
        switch (request.timeRangeType) {
            case "1":
                //noinspection MagicConstant
                calendar.set(year, month, day, 0, 0, 0);
                startTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                endTime = calendar.getTimeInMillis();
                break;
            case "2":
                //noinspection MagicConstant
                calendar.set(year, month, 1, 0, 0, 0);
                startTime = calendar.getTimeInMillis();
                calendar.add(Calendar.MONTH, 1);
                endTime = calendar.getTimeInMillis();
                break;
            case "3":
                calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                startTime = calendar.getTimeInMillis();
                calendar.add(Calendar.YEAR, 1);
                endTime = calendar.getTimeInMillis();
                break;
            case "4":
                startTime = 0;
                calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                calendar.add(Calendar.YEAR, 1);
                endTime = calendar.getTimeInMillis();
                break;
            default:
                return json(BaseResponse.CODE_FAILURE, "时间范围类型错误", response);
        }
        startTime = TimeKit.getDayStartTime(startTime);
        endTime = TimeKit.getDayEndTime(endTime);

        String startTimeStr = String.valueOf(startTime);
        String endTimeStr = String.valueOf(endTime);

        custWarrantyCost.start_time = startTimeStr;
        custWarrantyCost.end_time = endTimeStr;
        custWarrantyCost.time_range_type = request.timeRangeType;
        custWarrantyCost.manager_uuid = actionBean.managerUuid;

        custWarrantyBrokerage.start_time = startTimeStr;
        custWarrantyBrokerage.end_time = endTimeStr;
        custWarrantyBrokerage.time_range_type = request.timeRangeType;
        custWarrantyBrokerage.manager_uuid = actionBean.managerUuid;

        List<PremiumStatistic> custWarrantyCostStatistic = custWarrantyCostDao.findCustWarrantyCostStatistic(custWarrantyCost);
        List<BrokerageStatistic> custWarrantyBrokerageStatistic = custWarrantyBrokerageDao.findCustWarrantyBrokerageStatistic(custWarrantyBrokerage);

        LinkedHashMap<String, InsurancePolicyBean.InsurancePolicyStatisticItem> map = new LinkedHashMap<>();

        response.data = new InsurancePolicyBean.InsurancePolicyStatisticDetail();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        response.data.startTime = startTimeStr;
        response.data.startTimeText = sdf.format(new Date(startTime));
        response.data.endTime = endTimeStr;
        response.data.endTimeText = sdf.format(new Date(endTime));

        BigDecimal premium = new BigDecimal("0.00");
        int count = 0;
        if (custWarrantyCostStatistic != null && !custWarrantyCostStatistic.isEmpty()) {
            for (PremiumStatistic premiumStatistic : custWarrantyCostStatistic) {
                InsurancePolicyBean.InsurancePolicyStatisticItem item = new InsurancePolicyBean.InsurancePolicyStatisticItem(premiumStatistic.time_text);
                item.setPremiumStatisticModel(premiumStatistic);
                map.put(premiumStatistic.time_text, item);
                premium = premium.add(new BigDecimal(premiumStatistic.premium));

                if (StringKit.isInteger(premiumStatistic.insurance_policy_count)) {
                    count += Integer.valueOf(premiumStatistic.insurance_policy_count);
                }
            }
        }

        BigDecimal brokerage = new BigDecimal("0.00");
        if (custWarrantyBrokerageStatistic != null && !custWarrantyBrokerageStatistic.isEmpty()) {
            for (BrokerageStatistic brokerageStatistic : custWarrantyBrokerageStatistic) {
                InsurancePolicyBean.InsurancePolicyStatisticItem item = map.get(brokerageStatistic.time_text);
                if (item == null) {
                    item = new InsurancePolicyBean.InsurancePolicyStatisticItem(brokerageStatistic.time_text);
                    map.put(brokerageStatistic.time_text, item);
                }
                item.setBrokerageStatisticModel(brokerageStatistic);
                brokerage = brokerage.add(new BigDecimal(brokerageStatistic.brokerage));
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
                BigDecimal divide = brokerage.divide(premium, 6, BigDecimal.ROUND_HALF_DOWN);
                response.data.brokeragePercentage = String.valueOf(divide.doubleValue());
                divide = divide.multiply(new BigDecimal("100"));
                response.data.brokeragePercentageText = decimalFormat.format(divide.doubleValue()) + "%";
            } else {
                response.data.brokeragePercentage = "0.0";
                response.data.brokeragePercentageText = "0.00%";
            }

            BigDecimal bigDecimal = new BigDecimal(response.data.insurancePolicyCount);
            if (map.size() != 0 && bigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal divide = premium.divide(bigDecimal, 6, BigDecimal.ROUND_HALF_DOWN);
                response.data.averagePremium = decimalFormat.format(divide.doubleValue());
                response.data.averagePremiumText = "¥" + moneyFormat.format(new BigDecimal(response.data.averagePremium));
            } else {
                response.data.averagePremium = "0.00";
                response.data.averagePremiumText = "¥0.00";
            }

        }

        response.data.insurancePolicyList = dealPercentageByList(map, premium);

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 线下单导入
     * {@link #dealPercentageByList} 处理统计数据
     *
     * @param actionBean 请求bean
     * @return 保单统计json
     */
    @SuppressWarnings("ConstantConditions")
    public String offlineInsurancePolicyInput(ActionBean actionBean) {
        InsurancePolicyBean.OfflineInsurancePolicyInputRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.OfflineInsurancePolicyInputRequest.class);
        InsurancePolicyBean.OfflineInsurancePolicyInputResponse response = new InsurancePolicyBean.OfflineInsurancePolicyInputResponse();

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

        response.data = new InsurancePolicyBean.OfflineInsurancePolicyDetail();
        response.data.list = new ArrayList<>();

        boolean flag;
        Workbook wb;

        List<OfflineCustWarranty> errorList = new ArrayList<>();

        int successCount = 0;
        int failCount = 0;

        List<InsuranceCo> productCoList = productClient.getProductCoList(actionBean.managerUuid);
        List<ProductCategory> categoryList = productClient.getCategoryList("1");

        Map<String, String> columnFieldMap = ExcelModelKit.getColumnFieldMap(OfflineCustWarranty.OFFLINE_COLUMN_FIELD_LIST, 0);

        try {
            wb = WorkbookFactory.create(inputStream);
            String time = String.valueOf(System.currentTimeMillis());

            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    OfflineCustWarranty offlineCustWarranty = ExcelModelKit.createModel(OfflineCustWarranty.class, columnFieldMap, row);

                    if (offlineCustWarranty == null) {
                        continue;
                    }

                    if (offlineCustWarranty.isEmptyLine()) {
                        continue;
                    }

                    if (offlineCustWarranty.isTitle()) {
                        continue;
                    }

                    boolean success = offlineCustWarranty.isEnable();


                    if (!StringKit.isEmpty(offlineCustWarranty.insurance_company) && productCoList != null && !productCoList.isEmpty()) {
                        boolean b = false;
                        for (InsuranceCo insuranceCo : productCoList) {
                            if (StringKit.equals(insuranceCo.name, offlineCustWarranty.insurance_company)) {
                                b = true;
                                break;
                            }
                        }

                        if (!b) {
                            offlineCustWarranty.addErrorReason("insuranceCompany", "保险公司名称不存在");
                        }
                    }

                    if (!StringKit.isEmpty(offlineCustWarranty.insurance_type) && categoryList != null && !categoryList.isEmpty()) {
                        boolean b = false;
                        for (ProductCategory productCategory : categoryList) {
                            if (StringKit.equals(productCategory.name, offlineCustWarranty.insurance_type)) {
                                b = true;
                                break;
                            }
                        }

                        if (!b) {
                            offlineCustWarranty.addErrorReason("insuranceType", "保险产品名称不存在");
                        }
                    }

                    OfflineCustWarranty offlineInsurance = offlineCustWarrantyDao.findOfflineInsurancePolicyByWarrantyCode(offlineCustWarranty.warranty_code);

                    if (offlineInsurance != null) {
                        offlineCustWarranty.addErrorReason("保单号重复", "warrantyCode");
                        success = false;
                    } else if (success) {
                        offlineCustWarranty.manager_uuid = actionBean.managerUuid;
                        offlineCustWarranty.warranty_uuid = String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());
                        offlineCustWarranty.is_settlement = "0";
                        offlineCustWarranty.created_at = time;
                        offlineCustWarranty.updated_at = time;
                        offlineCustWarranty.state = "1";

                        long l = offlineCustWarrantyDao.addOfflineInsurancePolicy(offlineCustWarranty);

                        if (l <= 0) {
                            offlineCustWarranty.addErrorReason("添加数据失败", "ADD_FAIL");
                            success = false;
                        }
                    }

                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        InsurancePolicyBean.OfflineInsurancePolicy offlineInsurancePolicy = new InsurancePolicyBean.OfflineInsurancePolicy(offlineCustWarranty);
                        response.data.list.add(offlineInsurancePolicy);
                        offlineCustWarranty.reason = offlineInsurancePolicy.reason;
                        errorList.add(offlineCustWarranty);
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

        response.data.successCount = "成功：" + successCount + "条";
        response.data.failCount = "失败：" + failCount + "条";

        if (!flag && response.data.list != null && !response.data.list.isEmpty()) {
            OfflineCustWarranty titleModel = OfflineCustWarranty.getTitleModel();
            errorList.add(0, titleModel);

            List<ExcelModel<OfflineCustWarranty>> dataList = new ArrayList<>();
            for (OfflineCustWarranty offlineCustWarranty : errorList) {
                dataList.add(new ExcelModel<>(offlineCustWarranty));
            }

            byte[] data = ExcelModelKit.createExcelByteArray(dataList, columnFieldMap, new HashMap<>(), "导入失败保单数据");

            if (data == null) {
                return json(BaseResponse.CODE_INPUT_OFFLINE_FAILURE, "导入失败", response);
            }

            FileUpload.UploadByBase64Request fileUploadRequest = new FileUpload.UploadByBase64Request();
            fileUploadRequest.fileKey = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L));
            fileUploadRequest.fileName = fileUploadRequest.fileKey + ".xls";

            boolean upload = fileClient.upload(fileUploadRequest.fileKey, fileUploadRequest.fileName, data);

            if (upload) {
                response.data.excelFileKey = fileUploadRequest.fileKey;
                response.data.excelFileUrl = fileClient.getFileUrl(fileUploadRequest.fileKey);
                return json(BaseResponse.CODE_INPUT_OFFLINE_FAILURE, "部分导入失败", response);
            } else {
                return json(BaseResponse.CODE_INPUT_OFFLINE_FAILURE, "导入失败", response);
            }

        }

        return json((flag ? BaseResponse.CODE_SUCCESS : BaseResponse.CODE_INPUT_OFFLINE_FAILURE), (flag ? "导入成功" : "导入失败"), response);
    }

    /**
     * FINISH: 2018/6/8
     * 线下单列表
     *
     * @param actionBean 请求bean
     * @return 线下单列表json
     */
    public String getOfflineInsurancePolicyList(ActionBean actionBean) {
        InsurancePolicyBean.GetOfflineInsurancePolicyListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetOfflineInsurancePolicyListRequest.class);
        InsurancePolicyBean.GetOfflineInsurancePolicyListResponse response = new InsurancePolicyBean.GetOfflineInsurancePolicyListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();

        offlineCustWarranty.manager_uuid = actionBean.managerUuid;
        offlineCustWarranty.time_type = request.timeType;
        offlineCustWarranty.start_time = request.startTime;
        offlineCustWarranty.end_time = request.endTime;
        offlineCustWarranty.search_company = request.companyName;
        offlineCustWarranty.search_channel = request.channelName;
        offlineCustWarranty.search_product = request.productName;

        if (StringKit.isEmpty(request.pageNum)) {
            request.pageNum = "1";
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        offlineCustWarranty.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<OfflineCustWarranty> offlineInsurancePolicyListForManagerSystem = offlineCustWarrantyDao.findOfflineInsurancePolicyListForManagerSystem(offlineCustWarranty);
        long total = offlineCustWarrantyDao.findOfflineInsurancePolicyCountForManagerSystem(offlineCustWarranty);

        response.data = new ArrayList<>();
        if (offlineInsurancePolicyListForManagerSystem != null && !offlineInsurancePolicyListForManagerSystem.isEmpty()) {
            for (OfflineCustWarranty insurancePolicyModel : offlineInsurancePolicyListForManagerSystem) {
                response.data.add(new InsurancePolicyBean.OfflineInsurancePolicy(insurancePolicyModel));
            }
        }

        String lastId = "0";
        if (!response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取线下单列表成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 线下单详情
     *
     * @param actionBean 请求bean
     * @return 线下单详情json
     */
    public String getOfflineInsurancePolicyDetail(ActionBean actionBean) {
        InsurancePolicyBean.GetOfflineInsurancePolicyDetailRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetOfflineInsurancePolicyDetailRequest.class);
        InsurancePolicyBean.GetOfflineInsurancePolicyDetailResponse response = new InsurancePolicyBean.GetOfflineInsurancePolicyDetailResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        OfflineCustWarranty offlineInsurancePolicyByWarrantyUuid = offlineCustWarrantyDao.findOfflineInsurancePolicyByWarrantyUuid(request.warrantyUuid);

        if (offlineInsurancePolicyByWarrantyUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "该保单不存在", response);
        }

        response.data = new InsurancePolicyBean.OfflineInsurancePolicy(offlineInsurancePolicyByWarrantyUuid);

        return json(BaseResponse.CODE_SUCCESS, "获取线下单详情成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 获取线下单导入模板地址
     *
     * @param actionBean 请求bean
     * @return 线下单导入模板地址json
     */
    public String getOfflineInsurancePolicyInputTemplate(ActionBean actionBean) {
        // InsurancePolicy.GetOfflineInsurancePolicyInputTemplateRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetOfflineInsurancePolicyInputTemplateRequest.class);
        InsurancePolicyBean.GetOfflineInsurancePolicyInputTemplateResponse response = new InsurancePolicyBean.GetOfflineInsurancePolicyInputTemplateResponse();

        // actionBean.managerUuid

        String fileUrl = fileClient.getFileUrl("OFFLINE_20180702OFFLINE_20180702");

        InputStream inputStream = HttpClientKit.downloadFile(fileUrl);

        if (inputStream == null) {
            return json(BaseResponse.CODE_FAILURE, "获取模板文件失败", response);
        }

        boolean flag = true;
        Workbook sheets;

        String fileKey = actionBean.managerUuid + System.currentTimeMillis() + (Math.random() * 10000000L);
        String fileName = MD5Kit.MD5Digest(actionBean.managerUuid + System.currentTimeMillis()) + ".xlsx";

        try {
            sheets = WorkbookFactory.create(inputStream);

            if (sheets != null) {
                // Excel在一个sheet里面设置这个的时候，因为之前不知道设置数据大小，选择全列，会导致选择项出现空值
                // sheet2 C列
                List<InsuranceCo> productCoList = productClient.getProductCoList(actionBean.managerUuid);
                List<ExcelModel<String>> companyName = new ArrayList<>();
                if (productCoList != null && !productCoList.isEmpty()) {
                    for (InsuranceCo insuranceCo : productCoList) {
                        companyName.add(new ExcelModel<>(insuranceCo.name));
                    }
                }

                // sheet3 D列
                List<ProductCategory> categoryList = productClient.getCategoryList("2");
                List<ExcelModel<String>> productName = new ArrayList<>();
                if (categoryList != null && !categoryList.isEmpty()) {
                    for (ProductCategory productCategory : categoryList) {
                        productName.add(new ExcelModel<>(productCategory.name));
                    }
                }

                Sheet sheetAt1 = sheets.getSheetAt(0);
                Sheet sheetAt2 = sheets.getSheetAt(1);
                Sheet sheetAt3 = sheets.getSheetAt(2);

                ExcelModelKit.writeRow(sheetAt2, companyName, 2, 0, new HashMap<>());
                ExcelModelKit.writeRow(sheetAt3, productName, 3, 0, new HashMap<>());

                Map<String, String> columnFieldMap = ExcelModelKit.getColumnFieldMap(InsurancePolicyBean.CAR_FIELD_LIST, 0);
//                ExcelModelKit.autoSizeColumn(sheetAt1, columnFieldMap.size() - 1);

                flag = fileClient.upload(fileKey, fileName, ExcelModelKit.getWorkbookByteArray(sheets));
            }

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            flag = false;
        }

        if (flag) {
            String fileUrl1 = fileClient.getFileUrl(fileKey);
            if (!StringKit.isEmpty(fileUrl1)) {
                response.fileUrl = fileUrl1;
            } else {
                return json(BaseResponse.CODE_FAILURE, "获取模板文件失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "获取模板文件失败", response);
        }

        return json(BaseResponse.CODE_SUCCESS, "获取模板文件成功", response);
    }

    /**
     * FINISH: 2018/6/8
     * 保单报表
     *
     * @param actionBean 请求bean
     * @return 保单报表json
     */
    public String getInsurancePolicyStatementListForManagerSystem(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyStatementListRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyStatementListRequest.class);
        InsurancePolicyBean.GetInsurancePolicyStatementListResponse response = new InsurancePolicyBean.GetInsurancePolicyStatementListResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
        custWarrantyCost.search = request.searchKey;

        if (StringKit.isEmpty(request.startTime)) {
            long l = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
            custWarrantyCost.start_time = String.valueOf(l);
        } else {
            custWarrantyCost.start_time = request.startTime;
        }

        if (StringKit.isEmpty(request.endTime)) {
            custWarrantyCost.end_time = String.valueOf(System.currentTimeMillis());
        } else {
            custWarrantyCost.end_time = request.endTime;
        }

        custWarrantyCost.manager_uuid = actionBean.managerUuid;

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }
        custWarrantyCost.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<BrokerageStatisticList> insurancePolicyBrokerageStatisticList = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticList(custWarrantyCost);
        long total = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticListCount(custWarrantyCost);
        response.data = new ArrayList<>();
        String lastId = "0";

        if (insurancePolicyBrokerageStatisticList != null && !insurancePolicyBrokerageStatisticList.isEmpty()) {
            for (BrokerageStatisticList brokerageStatisticList : insurancePolicyBrokerageStatisticList) {
                CustWarrantyPerson holder = custWarrantyPersonDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(brokerageStatisticList.warranty_uuid);
                InsurancePolicyBean.InsurancePolicyBrokerageStatistic insurancePolicyBrokerageStatistic = new InsurancePolicyBean.InsurancePolicyBrokerageStatistic(brokerageStatisticList);
                insurancePolicyBrokerageStatistic.customerName = holder.name;
                if ("14463303497682968".equals(actionBean.managerUuid)) {
                    insurancePolicyBrokerageStatistic.customerName = "***";
                }
                insurancePolicyBrokerageStatistic.customerMobile = holder.phone;
                if (StringKit.isInteger(brokerageStatisticList.product_id)) {
                    ProductBean product = productClient.getProduct(Long.valueOf(brokerageStatisticList.product_id));
                    if (product != null) {
                        insurancePolicyBrokerageStatistic.insuranceName = product.insuranceCoName;
                        insurancePolicyBrokerageStatistic.productName = StringKit.isEmpty(product.displayName) ? product.name : product.displayName;
                    }
                }
                if (StringKit.isInteger(brokerageStatisticList.pay_category_id)) {
                    PayCategoryBean category = productClient.getOnePayCategory(Long.valueOf(brokerageStatisticList.pay_category_id));
                    if (category != null) {
                        insurancePolicyBrokerageStatistic.byStagesWay = category.name;
                    }
                }

                lastId = insurancePolicyBrokerageStatistic.costId;
                response.data.add(insurancePolicyBrokerageStatistic);
            }
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);

    }

    /**
     * FINISH: 2018/6/8
     * 根据（业管／代理人）和 付款时间 获取保单列表
     * {@link #dealInsurancePolicyResultList} 将列表处理为前段数据
     *
     * @param actionBean 请求bean
     * @return 保单列表json
     */
    public String getInsurancePolicyListByActualPayTime(ActionBean actionBean) {
        InsurancePolicyBean.GetInsurancePolicyListByActualPayTimeRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.GetInsurancePolicyListByActualPayTimeRequest.class);
        InsurancePolicyBean.GetInsurancePolicyListByActualPayTimeResponse response = new InsurancePolicyBean.GetInsurancePolicyListByActualPayTimeResponse();

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

        CustWarranty custWarranty = new CustWarranty();
        if (StringKit.equals(request.type, "1")) {
            custWarranty.manager_uuid = actionBean.managerUuid;
        } else if (StringKit.equals(request.type, "2")) {
            custWarranty.searchType = "2";
            AgentBean agentInfoByPersonIdManagerUuid = agentClient.getAgentInfoByPersonIdManagerUuid(actionBean.managerUuid, Long.valueOf(actionBean.userId));
            if (agentInfoByPersonIdManagerUuid == null) {
                return json(BaseResponse.CODE_PARAM_ERROR, "获取保单列表失败", response);
            }
            custWarranty.agent_id = String.valueOf(agentInfoByPersonIdManagerUuid.id);
        }

        if (!StringKit.isInteger(request.startTime)) {
            custWarranty.start_time = "";
        } else {
            custWarranty.start_time = request.startTime;
        }

        if (!StringKit.isInteger(request.endTime)) {
            custWarranty.end_time = "";
        } else {
            custWarranty.end_time = request.endTime;
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        custWarranty.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<CustWarranty> insurancePolicyListByActualPayTime = custWarrantyDao.findInsurancePolicyListByActualPayTime(custWarranty);
        long total = custWarrantyDao.findInsurancePolicyCountByActualPayTime(custWarranty);

        DealInsurancePolicyResultParameter parameter = new DealInsurancePolicyResultParameter();

        parameter.needLogo = true;
        parameter.needInsured = true;
        parameter.needChannel = false;
        parameter.needAgent = false;
        parameter.needSettlement = false;

        response.data = dealInsurancePolicyResultList(insurancePolicyListByActualPayTime, parameter);

        String lastId = "0";
        if (response.data != null && !response.data.isEmpty()) {
            lastId = response.data.get(response.data.size() - 1).id;
        }

        response.page = setPageBean(lastId, request.pageNum, request.pageSize, total, response.data == null ? 0 : response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单列表成功", response);
    }

    /**
     * FINISH: 2018/7/20
     * 修改线下单支付状态
     *
     * @param actionBean 请求bean
     * @return 修改结果json
     */
    public String updateOfflineInsurancePolicyPayStatus(ActionBean actionBean) {
        InsurancePolicyBean.UpdateOfflineInsurancePolicyPayStatusRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.UpdateOfflineInsurancePolicyPayStatusRequest.class);
        InsurancePolicyBean.UpdateOfflineInsurancePolicyPayStatusResponse response = new InsurancePolicyBean.UpdateOfflineInsurancePolicyPayStatusResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (StringKit.isEmpty(request.payStatus)) {
            return json(BaseResponse.CODE_FAILURE, "保单支付状态错误", response);
        }

        OfflineCustWarranty offlineInsurancePolicyByWarrantyUuid = offlineCustWarrantyDao.findOfflineInsurancePolicyByWarrantyUuid(request.warrantyUuid);

        if (offlineInsurancePolicyByWarrantyUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "该保单不存在", response);
        }

        OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
        offlineCustWarranty.warranty_uuid = request.warrantyUuid;
        offlineCustWarranty.updated_at = String.valueOf(System.currentTimeMillis());

        switch (request.payStatus) {
            case "1":
                offlineCustWarranty.pay_status = CustWarrantyCost.PAY_STATUS_WAIT;
                break;
            case "2":
                offlineCustWarranty.pay_status = CustWarrantyCost.PAY_STATUS_SUCCESS;
                break;
            default:
                return json(BaseResponse.CODE_FAILURE, "保单支付状态错误", response);
        }

        long l = 1;

        if (!StringKit.equals(offlineInsurancePolicyByWarrantyUuid.pay_status, offlineCustWarranty.pay_status)) {
            l = offlineCustWarrantyDao.updatePayStatusByWarrantyUuid(offlineCustWarranty);
        }

        String str;
        if (l > 0) {
            str = json(BaseResponse.CODE_SUCCESS, "更改保单支付状态成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "更改保单支付状态失败", response);
        }

        return str;
    }

    /**
     * FINISH: 2018/7/20
     * 删除线下单
     *
     * @param actionBean 请求bean
     * @return 删除结果json
     */
    public String deleteOfflineInsurancePolicy(ActionBean actionBean) {
        InsurancePolicyBean.DeleteOfflineInsurancePolicyRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicyBean.DeleteOfflineInsurancePolicyRequest.class);
        InsurancePolicyBean.DeleteOfflineInsurancePolicyResponse response = new InsurancePolicyBean.DeleteOfflineInsurancePolicyResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        OfflineCustWarranty offlineInsurancePolicyByWarrantyUuid = offlineCustWarrantyDao.findOfflineInsurancePolicyByWarrantyUuid(request.warrantyUuid);

        if (offlineInsurancePolicyByWarrantyUuid == null) {
            return json(BaseResponse.CODE_FAILURE, "该保单不存在", response);
        }

        OfflineCustWarranty offlineCustWarranty = new OfflineCustWarranty();
        offlineCustWarranty.warranty_uuid = request.warrantyUuid;
        offlineCustWarranty.updated_at = String.valueOf(System.currentTimeMillis());
        offlineCustWarranty.state = "0";

        long l = offlineCustWarrantyDao.updateStateByWarrantyUuid(offlineCustWarranty);

        String str;
        if (l > 0) {
            str = json(BaseResponse.CODE_SUCCESS, "删除成功", response);
        } else {
            str = json(BaseResponse.CODE_FAILURE, "删除失败", response);
        }

        return str;
    }

    // ====================================================================================================================================================================================

    /**
     * FINISH: 2018/6/8
     * 检查获取保单列表参数
     * <p>
     * {@link #getInsurancePolicyListForOnlineStore}
     * {@link #getInsurancePolicyListForManagerSystem}
     * {@link #downInsurancePolicyListForManagerSystem}
     *
     * @param request              获取保单列表请求
     * @param custWarranty 获取保单列表数据库查询model
     * @return 是否有参数异常（如果有，此为异常）
     */
    private String checkGetInsurancePolicyListParams(InsurancePolicyBean.GetInsurancePolicyListRequest request, CustWarranty custWarranty) {
        if (!StringKit.isEmpty(request.warrantyType) && !StringKit.isNumeric(request.warrantyType)) {
            return "保单类型错误";
        }

        if (request.warrantyType == null) {
            request.warrantyType = "";
        }

        custWarranty.type = request.warrantyType;

        if (!StringKit.isEmpty(request.warrantyStatus)) {
            switch (request.warrantyStatus) {
                case "1":
                case "2":
                case "3":
                case "4":
                case "90":
                    custWarranty.search_warranty_string = request.warrantyStatus;
                    break;
                default:
                    return "保单状态错误";
            }
        }
        custWarranty.queryWay = request.queryWay;
        custWarranty.warranty_status = request.warrantyStatus;

        custWarranty.search = request.searchKey;
        // 1-保单号 2-代理人 3-投保人 4-车牌号 5-保险公司
        if (!StringKit.isEmpty(request.searchType) && (!StringKit.isNumeric(request.searchType) || Integer.valueOf(request.searchType) < 1 || Integer.valueOf(request.searchType) > 7)) {
            return "搜索类型错误";
        } else if (StringKit.isEmpty(request.searchType)) {
            request.searchType = "";
            request.searchKey = "";
        }

        if (!StringKit.isEmpty(request.searchType) && StringKit.isEmpty(request.searchKey)) {
            return "搜索关键字";
        }

        custWarranty.searchType = request.searchType;

        if (!StringKit.isEmpty(request.startTime) && !StringKit.isInteger(request.startTime)) {
            return "开始时间有误";
        }
        custWarranty.start_time = request.startTime;
        if (!StringKit.isEmpty(request.endTime) && !StringKit.isInteger(request.endTime)) {
            return "结束时间有误";
        }
        custWarranty.end_time = request.endTime;

        if (!StringKit.isEmpty(request.startTime) && !StringKit.isEmpty(request.endTime) && StringKit.isEmpty(request.timeType)) {
            request.timeType = "1";
        }

        custWarranty.time_type = request.timeType;

        if (StringKit.equals(request.searchType, "5")) {
            request.insuranceCompanyKey = request.searchKey;
        }

        if (!StringKit.isEmpty(request.insuranceCompanyKey) || StringKit.equals(request.searchType, "5")) {
            InsuranceCompanyBean insuranceCompanyBean = new InsuranceCompanyBean();
            insuranceCompanyBean.name = request.insuranceCompanyKey;
            insuranceCompanyBean.managerUuid = custWarranty.manager_uuid;
            List<InsuranceCompanyBean> listInsuranceCompany = companyClient.getListInsuranceCompany(insuranceCompanyBean);
            if (listInsuranceCompany != null && !listInsuranceCompany.isEmpty()) {
                int size = listInsuranceCompany.size();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    InsuranceCompanyBean insuranceCompanyBean1 = listInsuranceCompany.get(i);
                    sb.append(insuranceCompanyBean1.id);
                    if (i != size - 1) {
                        sb.append(",");
                    }
                }
                custWarranty.insurance_co_id_string = sb.toString();
            } else {
                custWarranty.insurance_co_id_string = "-1";
            }
        }

        if (StringKit.equals(request.searchType, "6")) {
            request.insuranceProductKey = request.searchKey;
        }

        if (!StringKit.isEmpty(request.insuranceProductKey)) {
            List<ProductBean> listProduct = productClient.getListProduct(request.insuranceProductKey, custWarranty.manager_uuid);
            if (listProduct != null && !listProduct.isEmpty()) {
                int size = listProduct.size();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < listProduct.size(); i++) {
                    ProductBean productBean = listProduct.get(i);
                    sb.append(productBean.id);
                    if (i != size - 1) {
                        sb.append(",");
                    }
                }
                custWarranty.product_id_string = sb.toString();
            } else {
                custWarranty.product_id_string = "-1";
            }
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }

        if (StringKit.equals(request.searchType, "2")) {
            getAgentMap(custWarranty.manager_uuid, request.searchKey, custWarranty);
        }

        custWarranty.page = setPage(request.lastId, request.pageNum, request.pageSize);

        return null;
    }

    static class DealInsurancePolicyResultParameter {
        boolean needLogo = false;
        boolean needInsured = false;
        boolean needChannel = false;
        boolean needAgent = false;
        boolean needSettlement = false;
    }

    /**
     * FINISH: 2018/6/8
     * 将列表处理为前台数据
     *
     * @param custWarrantyList 保单列表
     * @param parameter                处理结果参数
     * @return 前台数据列表
     */
    private List<InsurancePolicyBean.GetInsurancePolicyItemBean> dealInsurancePolicyResultList(List<CustWarranty> custWarrantyList, DealInsurancePolicyResultParameter parameter) {
        List<InsurancePolicyBean.GetInsurancePolicyItemBean> result = new ArrayList<>();
        if (custWarrantyList == null) {
            return result;
        }

        Set<String> productId = new HashSet<>();
        Set<String> payCategoryId = new HashSet<>();
        Set<String> channelId = new HashSet<>();
        Set<String> agentId = new HashSet<>();
        HashMap<String, String> fileUrl = new HashMap<>();
        HashMap<String, Boolean> file = new HashMap<>();

        String managerUuid = "";

        if (!custWarrantyList.isEmpty()) {
            CustWarrantyPerson custWarrantyPerson = new CustWarrantyPerson();
            StringBuilder warrantyUuidString = new StringBuilder();
            int size1 = custWarrantyList.size();
            Bill bill = new Bill();
            for (int i = 0; i < size1; i++) {
                CustWarranty policyListByWarrantyStatusOrSearch = custWarrantyList.get(i);
                managerUuid = policyListByWarrantyStatusOrSearch.manager_uuid;
                if ("14463303497682968".equals(policyListByWarrantyStatusOrSearch.manager_uuid)) {
                    policyListByWarrantyStatusOrSearch.policy_holder_name = "***";
                }
                InsurancePolicyBean.GetInsurancePolicyItemBean model = new InsurancePolicyBean.GetInsurancePolicyItemBean(policyListByWarrantyStatusOrSearch);

                productId.add(model.productId);
                payCategoryId.add(model.payCategoryId);
                channelId.add(model.channelId);
                agentId.add(model.agentId);

                if (parameter.needSettlement) {
                    CustWarrantyCost custWarrantyCostModel = new CustWarrantyCost();
                    custWarrantyCostModel.warranty_uuid = model.warrantyUuid;
                    List<CustWarrantyCost> custWarrantyCost = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

                    if (custWarrantyCost != null && !custWarrantyCost.isEmpty()) {
                        model.isSettlement = Bill.SETTLEMENT_STATE_NOT;
                        for (CustWarrantyCost warrantyCostModel : custWarrantyCost) {
                            if (StringKit.equals(warrantyCostModel.is_settlement, Bill.SETTLEMENT_STATE_ALREADY)) {
                                model.isSettlement = Bill.SETTLEMENT_STATE_ALREADY;
                                break;
                            }
                        }
                    } else {
                        model.isSettlement = Bill.SETTLEMENT_STATE_NOT;
                    }

                    model.isSettlementText = bill.isSettlementText(model.isSettlement);
                }

                if (parameter.needInsured) {
                    warrantyUuidString.append(model.warrantyUuid);

                    if (i != size1 - 1) {
                        warrantyUuidString.append(",");
                    }
//                    List<InsuranceParticipantModel> insuranceParticipantInsuredByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantInsuredByWarrantyUuid(model.warrantyUuid);
//                    if (insuranceParticipantInsuredByWarrantyUuid != null && !insuranceParticipantInsuredByWarrantyUuid.isEmpty()) {
//                        int size = insuranceParticipantInsuredByWarrantyUuid.size();
//                        String name = insuranceParticipantInsuredByWarrantyUuid.get(0).name;
//                        if (size > 1) {
//                            model.insuredText = name + "等" + size + "人";
//                            StringBuilder sb = new StringBuilder();
//                            for (int i = 0; i < insuranceParticipantInsuredByWarrantyUuid.size(); i++) {
//                                sb.append(insuranceParticipantInsuredByWarrantyUuid.get(i).name);
//                                if (i != size - 1) {
//                                    sb.append(",");
//                                }
//                            }
//                            model.insuredDetailText = sb.toString();
//                        } else {
//                            model.insuredText = name;
//                            model.insuredDetailText = name;
//                        }
//
//                    }
                }

//                if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
//
//                } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_PERSON)) {
//
//                } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_TEAM)) {
//
//                }
                result.add(model);
            }

            HashMap<String, CustWarrantyPerson> insuredName = new HashMap<>();

            if (parameter.needInsured && warrantyUuidString.length() > 0) {
                custWarrantyPerson.warranty_uuid_string = warrantyUuidString.toString();
                List<CustWarrantyPerson> insuranceParticipantInsuredNameByWarrantyUuids = custWarrantyPersonDao.findInsuranceParticipantInsuredNameByWarrantyUuids(custWarrantyPerson);

                if (insuranceParticipantInsuredNameByWarrantyUuids != null && !insuranceParticipantInsuredNameByWarrantyUuids.isEmpty()) {
                    for (CustWarrantyPerson insuranceParticipantInsuredNameByWarrantyUuid : insuranceParticipantInsuredNameByWarrantyUuids) {
                        insuredName.put(insuranceParticipantInsuredNameByWarrantyUuid.warranty_uuid, insuranceParticipantInsuredNameByWarrantyUuid);
                    }
                }
            }


            List<String> ids = new ArrayList<>(productId);
            Map<String, ProductBean> productMap = new HashMap<>();

            if (!ids.isEmpty()) {
                List<ProductBean> productList = productClient.getProductList(ids);

                if (productList != null && !productList.isEmpty()) {
                    for (ProductBean productBean : productList) {
                        productMap.put(String.valueOf(productBean.id), productBean);
                    }
                }
            }

            List<String> payCategoryIds = new ArrayList<>(payCategoryId);
            Map<String, ProductPayCategoryBean> payCategoryMap = new HashMap<>();

            if (!ids.isEmpty()) {
                List<ProductPayCategoryBean> payCategoryBeans = productClient.listProductPayCategory(payCategoryIds);

                if (payCategoryBeans != null && !payCategoryBeans.isEmpty()) {
                    for (ProductPayCategoryBean productPayCategoryBean : payCategoryBeans) {
                        payCategoryMap.put(String.valueOf(productPayCategoryBean.id), productPayCategoryBean);
                    }
                }
            }


            Map<String, ChannelBean> channelMap = new HashMap<>();
            if (parameter.needChannel) {
                List<String> channelIds = new ArrayList<>(channelId);

                if (!channelIds.isEmpty()) {
                    List<ChannelBean> channelList = channelClient.getChannelListByIds(channelIds);

                    if (channelList != null && !channelList.isEmpty()) {
                        for (ChannelBean channelBean : channelList) {
                            channelMap.put(channelBean.id, channelBean);
                        }
                    }
                }
            }

            Map<String, AgentBean> agentMap = new HashMap<>();
            if (parameter.needAgent) {
                List<String> agentIds = new ArrayList<>(agentId);
                if (!ids.isEmpty()) {
                    List<AgentBean> agentList = agentClient.getAgentListByIds(agentIds);
                    if (agentList != null && !agentList.isEmpty()) {
                        for (AgentBean agentBean : agentList) {
                            agentMap.put(String.valueOf(agentBean.id), agentBean);
                        }
                    }
                }
            }

            for (InsurancePolicyBean.GetInsurancePolicyItemBean getInsurancePolicyItemBean : result) {

                ProductBean productBean = productMap.get(getInsurancePolicyItemBean.productId);
                if (productBean != null) {
                    getInsurancePolicyItemBean.productName = productBean.name;
                    getInsurancePolicyItemBean.insuranceProductName = productBean.displayName;
                    getInsurancePolicyItemBean.insuranceCompanyName = productBean.insuranceCoName;

                    if (!StringKit.isEmpty(productBean.code)) {
                        String[] split = productBean.code.split("_");

                        String url = null;
                        if (split.length > 1 && parameter.needLogo) {
                            Boolean aBoolean1 = file.get(split[0]);
                            if (aBoolean1 == null) {
                                url = fileClient.getFileUrl("property_key_" + split[0]);
                                fileUrl.put(split[0], url);
                                file.put(split[0], !StringKit.isEmpty(url));
                            } else {
                                if (aBoolean1) {
                                    url = fileUrl.get(split[0]);
                                }
                            }
                            getInsurancePolicyItemBean.insuranceCompanyLogo = url;
                        }
                    }

                }

                if (parameter.needChannel && !channelMap.isEmpty()) {
                    ChannelBean channelBean = channelMap.get(getInsurancePolicyItemBean.channelId);
                    if (channelBean != null) {
                        getInsurancePolicyItemBean.channelName = channelBean.name;
                    }
                }

                ProductPayCategoryBean productPayCategoryBean = payCategoryMap.get(getInsurancePolicyItemBean.payCategoryId);
                if (productPayCategoryBean != null) {
                    getInsurancePolicyItemBean.payCategoryName = productPayCategoryBean.name;
                }

                if (parameter.needAgent && !agentMap.isEmpty()) {
                    AgentBean agentBean = agentMap.get(getInsurancePolicyItemBean.agentId);
                    if (agentBean != null) {
                        getInsurancePolicyItemBean.agentName = agentBean.name;
                    }
                }

                if (parameter.needInsured && !insuredName.isEmpty()) {
                    CustWarrantyPerson custWarrantyPerson1 = insuredName.get(getInsurancePolicyItemBean.warrantyUuid);
                    if (custWarrantyPerson1 != null && !StringKit.isEmpty(custWarrantyPerson1.name)) {
                        if (!"14463303497682968".equals(managerUuid)) {
                            if (custWarrantyPerson1.count > 1) {
                                String[] split = custWarrantyPerson1.name.split(",");
                                getInsurancePolicyItemBean.insuredText = split[0] + "等" + custWarrantyPerson1.count + "人";
                            } else {
                                getInsurancePolicyItemBean.insuredText = custWarrantyPerson1.name;
                            }
                            getInsurancePolicyItemBean.insuredDetailText = custWarrantyPerson1.name;
                        } else {
                            getInsurancePolicyItemBean.insuredText = "***";
                            getInsurancePolicyItemBean.insuredDetailText = "***";

                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * FINISH: 2018/6/8
     * 根据关键字获取代理人id
     *
     * @param managerUuid          业管唯一标识
     * @param searchKey            搜索关键字
     * @param custWarranty 数据库查询model
     * @return 代理人id-bean map
     */
    private Map<String, AgentBean> getAgentMap(String managerUuid, String searchKey, CustWarranty custWarranty) {
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
        custWarranty.agent_id_string = sb.toString();
        return agentMap;
    }

    /**
     * FINISH: 2018/6/8
     * 根据warrantyUuid获取保单详情
     *
     * @param warrantyUuid warrantyUuid
     * @param response     响应response
     * @return 保单详情json
     */
    private String getInsurancePolicyDetail(String warrantyUuid, InsurancePolicyBean.GetInsurancePolicyDetailResponse response) {
        CustWarranty insurancePolicyDetailByWarrantyCode = custWarrantyDao.findInsurancePolicyDetailByWarrantyUuid(warrantyUuid);
        String str;
        if (insurancePolicyDetailByWarrantyCode != null) {
            List<CustWarrantyPerson> insuranceParticipantByWarrantyCode = custWarrantyPersonDao.findInsuranceParticipantByWarrantyUuid(warrantyUuid);

            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
            custWarrantyCost.warranty_uuid = insurancePolicyDetailByWarrantyCode.warranty_uuid;

            List<CustWarrantyCost> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCost);

            CustWarrantyCostListResult custWarrantyCostListResult = dealCustWarrantyCostList(custWarrantyCostByWarrantyUuid);

            response.data = new InsurancePolicyBean.GetInsurancePolicyDetail(insurancePolicyDetailByWarrantyCode, custWarrantyCostListResult.premium, custWarrantyCostListResult.payMoney, custWarrantyCostListResult.taxMoney, custWarrantyCostListResult.warrantyStatusForPay, custWarrantyCostListResult.warrantyStatusForPayText);

            response.data.costList = custWarrantyCostListResult.list;

            response.data.insuredList = new ArrayList<>();
            response.data.beneficiaryList = new ArrayList<>();

            response.data.brokerageList = new ArrayList<>();
            List<CustWarrantyBrokerage> custWarrantyBrokerageByWarrantyUuid = custWarrantyBrokerageDao.findCustWarrantyBrokerageByWarrantyUuid(warrantyUuid);
            if (custWarrantyBrokerageByWarrantyUuid != null && !custWarrantyBrokerageByWarrantyUuid.isEmpty()) {
                for (CustWarrantyBrokerage custWarrantyBrokerage : custWarrantyBrokerageByWarrantyUuid) {
                    if (!StringKit.equals(insurancePolicyDetailByWarrantyCode.warranty_status, CustWarranty.POLICY_STATUS_INVALID) && !StringKit.equals(insurancePolicyDetailByWarrantyCode.warranty_status, CustWarranty.POLICY_STATUS_PENDING)) {
                        custWarrantyBrokerage.warranty_money = "0.00";
                        custWarrantyBrokerage.ins_money = "0.00";
                        custWarrantyBrokerage.manager_money = "0.00";
                        custWarrantyBrokerage.channel_money = "0.00";
                        custWarrantyBrokerage.agent_money = "0.00";
                    }
                    response.data.brokerageList.add(new InsurancePolicyBean.CustWarrantyBrokerage(custWarrantyBrokerage));
                }
            }

            ProductBean product = null;
            if (StringKit.isInteger(insurancePolicyDetailByWarrantyCode.product_id)) {
                product = productClient.getProduct(Long.valueOf(insurancePolicyDetailByWarrantyCode.product_id));
            }

            if (StringKit.isInteger(insurancePolicyDetailByWarrantyCode.pay_category_id)) {
                ProductPayCategoryBean productPayCategory = productClient.getProductPayCategory(Long.valueOf(insurancePolicyDetailByWarrantyCode.pay_category_id));
                if (productPayCategory != null) {
                    response.data.payCategoryName = productPayCategory.name;
                }
            }

            if (insuranceParticipantByWarrantyCode != null && !insuranceParticipantByWarrantyCode.isEmpty()) {
                if (product != null) {
                    response.data.productName = product.name;
                    response.data.insuranceProductName = product.displayName;
                    response.data.insuranceCompanyName = product.insuranceCoName;

                    if (!StringKit.isEmpty(product.code)) {
                        String[] split = product.code.split("_");
                        if (split.length > 1) {
                            response.data.insuranceCompanyLogo = fileClient.getFileUrl("property_key_" + split[0]);
                        }
                    }

                }

                for (CustWarrantyPerson custWarrantyPerson : insuranceParticipantByWarrantyCode) {
                    InsurancePolicyBean.InsurancePolicyParticipantInfo insurancePolicyParticipantInfo = new InsurancePolicyBean.InsurancePolicyParticipantInfo(custWarrantyPerson);

                    if ("14463303497682968".equals(insurancePolicyDetailByWarrantyCode.manager_uuid)) {
                        insurancePolicyParticipantInfo.name = "***";
                        insurancePolicyParticipantInfo.cardCode = "******************";
                    }

                    if (StringKit.equals(insurancePolicyParticipantInfo.type, CustWarrantyPerson.TYPE_POLICYHOLDER)) {

                        response.data.policyHolder = insurancePolicyParticipantInfo;
                    } else if (StringKit.equals(insurancePolicyParticipantInfo.type, CustWarrantyPerson.TYPE_INSURED)) {
                        response.data.insuredList.add(insurancePolicyParticipantInfo);
                    } else if (StringKit.equals(insurancePolicyParticipantInfo.type, CustWarrantyPerson.TYPE_BENEFICIARY)) {
                        response.data.beneficiaryList.add(insurancePolicyParticipantInfo);
                    }
                }
            }

            if (StringKit.equals(insurancePolicyDetailByWarrantyCode.type, CustWarranty.POLICY_TYPE_CAR)) {
                // 车险
                CustWarrantyCar oneByWarrantyCode = carInfoDao.findOneByWarrantyUuid(warrantyUuid);
                response.data.carInfo = new InsurancePolicyBean.CarInfo(oneByWarrantyCode);
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

    /**
     * FINISH: 2018/6/8
     * 处理保单统计信息
     *
     * @param map     统计item
     * @param premium 保费
     * @return 保单统计item list
     */
    private List<InsurancePolicyBean.InsurancePolicyStatisticItem> dealPercentageByList(LinkedHashMap<String, InsurancePolicyBean.InsurancePolicyStatisticItem> map, BigDecimal premium) {
        List<InsurancePolicyBean.InsurancePolicyStatisticItem> result = new ArrayList<>();
        Set<String> strings = map.keySet();
        if (!strings.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            DecimalFormat moneyFormat = new DecimalFormat("###,###,###,###,##0.00");
            for (String string : strings) {
                InsurancePolicyBean.InsurancePolicyStatisticItem item = map.get(string);

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
                        BigDecimal divide = itemPremium.divide(insurancePolicyCount, 6, BigDecimal.ROUND_HALF_DOWN);
                        item.averagePremium = moneyFormat.format(divide.doubleValue());
                        item.averagePremiumText = "¥" + item.averagePremium;
                    } else {
                        item.averagePremium = "0.00";
                        item.averagePremiumText = "¥0.00";
                    }

                    if (premium.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal divide = itemPremium.divide(premium, 6, BigDecimal.ROUND_HALF_DOWN);
                        item.premiumPercentage = String.valueOf((divide.doubleValue()));
                        divide = divide.multiply(new BigDecimal("100"));
                        item.premiumPercentageText = decimalFormat.format(divide.doubleValue()) + "%";
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
                    BigDecimal divide = itemBrokerage.divide(itemPremium, 6, BigDecimal.ROUND_HALF_DOWN);
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

    /**
     * FINISH: 2018/6/8
     * 保单付款结果
     */
    static class CustWarrantyCostListResult {
        // 保费
        BigDecimal premium = new BigDecimal("0.00");
        // 付款金额
        BigDecimal payMoney = new BigDecimal("0.00");
        // 税费
        BigDecimal taxMoney = new BigDecimal("0.00");
        String warrantyStatusForPay = "";
        String warrantyStatusForPayText = "";
        List<InsurancePolicyBean.CustWarrantyCost> list;
    }

    /**
     * FINISH: 2018/6/8
     * 处理保单付款结果
     * {@link CustWarrantyCostListResult}
     *
     * @param list 保单付款记录
     * @return 保单付款结果bean
     */
    private CustWarrantyCostListResult dealCustWarrantyCostList(List<CustWarrantyCost> list) {
        CustWarrantyCostListResult custWarrantyCostListResult = new CustWarrantyCostListResult();
        custWarrantyCostListResult.list = new ArrayList<>();

        boolean isFindLast;
        int size1 = list.size();

        for (int i = size1 - 1; i > -1; i--) {
            CustWarrantyCost custWarrantyCost1 = list.get(i);
            isFindLast = StringKit.equals(custWarrantyCost1.pay_status, CustWarrantyCost.PAY_STATUS_SUCCESS);

            if (StringKit.isNumeric(custWarrantyCost1.premium)) {
                custWarrantyCostListResult.premium = custWarrantyCostListResult.premium.add(new BigDecimal(custWarrantyCost1.premium));
            }

            if (StringKit.isNumeric(custWarrantyCost1.tax_money)) {
                custWarrantyCostListResult.taxMoney = custWarrantyCostListResult.taxMoney.add(new BigDecimal(custWarrantyCost1.tax_money));
            }

            if (StringKit.isNumeric(custWarrantyCost1.pay_money)) {
                custWarrantyCostListResult.payMoney = custWarrantyCostListResult.payMoney.add(new BigDecimal(custWarrantyCost1.pay_money));
            }

            if (isFindLast || i == 0) {
                custWarrantyCostListResult.warrantyStatusForPay = custWarrantyCost1.pay_status;
                custWarrantyCostListResult.warrantyStatusForPayText = custWarrantyCost1.payStatusText(custWarrantyCost1.pay_status);
            }

            custWarrantyCostListResult.list.add(new InsurancePolicyBean.CustWarrantyCost(custWarrantyCost1));
        }

        return custWarrantyCostListResult;
    }

    public String setTest() {

        CustWarranty custWarranty = new CustWarranty();
        custWarranty.page = setPage(null, "1", "10");

        custWarranty.manager_uuid = "2";
        custWarranty.person_type = "1";
        custWarranty.card_code = "110101199312160523";
        custWarranty.card_type = "1";

        List<CustWarranty> insuranceRecordListByManagerUuid = custWarrantyDao.findInsuranceRecordListByManagerUuid(custWarranty);

        return JsonKit.bean2Json(insuranceRecordListByManagerUuid);
    }

}