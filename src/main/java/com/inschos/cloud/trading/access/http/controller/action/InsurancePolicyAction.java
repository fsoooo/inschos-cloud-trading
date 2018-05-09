package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.rpc.bean.AccountBean;
import com.inschos.cloud.trading.access.rpc.client.AccountClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.*;
import com.inschos.cloud.trading.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private AccountClient accountClient;

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
        LinkedHashMap<String, String> warrantyStatusMap = InsurancePolicyModel.getWarrantyStatusMap();
        Set<String> strings = warrantyStatusMap.keySet();

        for (String string : strings) {
            InsurancePolicy.GetInsurancePolicyStatus getInsurancePolicyStatus = new InsurancePolicy.GetInsurancePolicyStatus();
            getInsurancePolicyStatus.value = string;
            getInsurancePolicyStatus.valueText = warrantyStatusMap.get(string);
            response.data.add(getInsurancePolicyStatus);
        }

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

        if (StringKit.isEmpty(request.lastId)) {
            request.lastId = "0";
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

            if (insurancePolicyListByWarrantyStatusOrSearch != null && !insurancePolicyListByWarrantyStatusOrSearch.isEmpty()) {

                // TODO: 2018/4/17 真实的，目前不能用
                // List<InsuranceConciseInfo> insuranceConciseInfo = insuranceServiceClient.insList();

//                HashMap<String, InsuranceConciseInfo> map = new HashMap<>();
//
//                for (InsuranceConciseInfo conciseInfo : insuranceConciseInfo) {
//                    map.put(conciseInfo.id, conciseInfo);
//                }

                CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
                long time = System.currentTimeMillis();
                for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyListByWarrantyStatusOrSearch) {

                    custWarrantyCostModel.warranty_uuid = policyListByWarrantyStatusOrSearch.warranty_uuid;

                    List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

                    BigDecimal premium = new BigDecimal("0.00");
                    BigDecimal payMoney = new BigDecimal("0.00");

                    boolean isFindLast = false;
                    int size1 = custWarrantyCostByWarrantyUuid.size();
                    String warrantyStatusForPay = "";
                    String warrantyStatusForPayText = "";

                    for (int i = size1 - 1; i > -1; i--) {
                        CustWarrantyCostModel custWarrantyCostModel1 = custWarrantyCostByWarrantyUuid.get(i);
                        if (StringKit.isNumeric(custWarrantyCostModel1.premium)) {
                            premium = premium.add(new BigDecimal(custWarrantyCostModel1.premium));
                        }

                        if (StringKit.isNumeric(custWarrantyCostModel1.pay_money)) {
                            payMoney = payMoney.add(new BigDecimal(custWarrantyCostModel1.pay_money));
                        }

                        if (!isFindLast) {
                            isFindLast = !StringKit.isEmpty(custWarrantyCostModel1.actual_pay_time);
                        }

                        if (isFindLast || i == 0) {
                            warrantyStatusForPay = custWarrantyCostModel1.pay_status;
                            warrantyStatusForPayText = custWarrantyCostModel1.payStatusText(custWarrantyCostModel1.pay_status);
                        }
                    }

                    InsurancePolicy.GetInsurancePolicy insurancePolicy = new InsurancePolicy.GetInsurancePolicy(policyListByWarrantyStatusOrSearch, premium, payMoney, warrantyStatusForPay, warrantyStatusForPayText);

                    if (StringKit.equals(insurancePolicy.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                        CarInfoModel bjCodeFlagAndBizIdByWarrantyUuid = carInfoDao.findBjCodeFlagAndBizIdByWarrantyUuid(insurancePolicy.warrantyUuid);
                        insurancePolicy.bjCodeFlag = bjCodeFlagAndBizIdByWarrantyUuid.bj_code_flag;
                        insurancePolicy.bizId = bjCodeFlagAndBizIdByWarrantyUuid.biz_id;
                    }

//                    InsuranceConciseInfo product = map.get(insurancePolicy.productId);
//                    if (product != null) {
//                        insurancePolicy.insuranceProductName = product.ins_name;
//                        insurancePolicy.insuranceCompanyName = product.company_name;
//                    }

                    insurancePolicy.insuranceProductName = "产品名";
                    insurancePolicy.insuranceCompanyName = "保险公司名";

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
                    response.data.add(insurancePolicy);
                }
            }

            response.page = setPageBean(request.pageNum, request.pageSize, total, response.data.size());

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

        insurancePolicyModel.type = request.warrantyType;

        if (StringKit.isEmpty(request.warrantyStatus)) {
            insurancePolicyModel.warranty_status = "0";
        } else {
            insurancePolicyModel.warranty_status = request.warrantyStatus;
        }

        insurancePolicyModel.search = request.searchKey;

        if (StringKit.isEmpty(request.startTime)) {
            insurancePolicyModel.start_time = "0";
        } else {
            insurancePolicyModel.start_time = request.startTime;
        }

        if (StringKit.isEmpty(request.endTime)) {
            insurancePolicyModel.end_time = "0";
        } else {
            insurancePolicyModel.end_time = request.endTime;
        }

        if (StringKit.isEmpty(request.warrantyFrom)) {
            insurancePolicyModel.warranty_from = "0";
        } else {
            insurancePolicyModel.warranty_from = request.warrantyFrom;
        }

        if (StringKit.isEmpty(request.channelId)) {
            insurancePolicyModel.channel_id = request.channelId;
        }

        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        if (StringKit.isEmpty(request.lastId)) {
            request.lastId = "0";
        }

        if (StringKit.isEmpty(request.pageNum)) {
            request.pageNum = "1";
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "10";
        }


//        AccountBean agentBean = accountClientService.findByUuid(insurancePolicyModel.agent_auuid);
//        agentBean.userId

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelId(insurancePolicyModel);
        response.data = new ArrayList<>();

        for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyListByWarrantyStatusOrSearch) {

            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            long time = System.currentTimeMillis();
            custWarrantyCostModel.warranty_uuid = policyListByWarrantyStatusOrSearch.warranty_uuid;

            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

            BigDecimal premium = new BigDecimal("0.00");
            BigDecimal payMoney = new BigDecimal("0.00");

            boolean isFindLast = false;
            int size1 = custWarrantyCostByWarrantyUuid.size();
            String warrantyStatusForPay = "";
            String warrantyStatusForPayText = "";

            for (int i = size1 - 1; i > -1; i--) {
                CustWarrantyCostModel custWarrantyCostModel1 = custWarrantyCostByWarrantyUuid.get(i);
                if (StringKit.isNumeric(custWarrantyCostModel1.premium)) {
                    premium = premium.add(new BigDecimal(custWarrantyCostModel1.premium));
                }

                if (StringKit.isNumeric(custWarrantyCostModel1.pay_money)) {
                    payMoney = payMoney.add(new BigDecimal(custWarrantyCostModel1.pay_money));
                }

                if (!isFindLast) {
                    isFindLast = !StringKit.isEmpty(custWarrantyCostModel1.actual_pay_time);
                }

                if (isFindLast || i == 0) {
                    warrantyStatusForPay = custWarrantyCostModel1.pay_status;
                    warrantyStatusForPayText = custWarrantyCostModel1.payStatusText(custWarrantyCostModel1.pay_status);
                }
            }

            InsurancePolicy.GetInsurancePolicyForManagerSystem getInsurancePolicyForManagerSystem = new InsurancePolicy.GetInsurancePolicyForManagerSystem(policyListByWarrantyStatusOrSearch, premium, payMoney, warrantyStatusForPay, warrantyStatusForPayText);

            if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
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
            } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_PERSON)) {

            } else if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_TEAM)) {

            }
            response.data.add(getInsurancePolicyForManagerSystem);
        }

        long total = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelIdCount(insurancePolicyModel);

        response.page = setPageBean(request.pageNum, request.pageSize, total, response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取列表成功", response);
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
            long time = System.currentTimeMillis();
            custWarrantyCostModel.warranty_uuid = insurancePolicyDetailByWarrantyCode.warranty_uuid;

            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);

            BigDecimal premium = new BigDecimal("0.00");
            BigDecimal payMoney = new BigDecimal("0.00");

            boolean isFindLast = false;
            int size1 = custWarrantyCostByWarrantyUuid.size();
            String warrantyStatusForPay = "";
            String warrantyStatusForPayText = "";

            for (int i = size1 - 1; i > -1; i--) {
                CustWarrantyCostModel custWarrantyCostModel1 = custWarrantyCostByWarrantyUuid.get(i);
                if (StringKit.isNumeric(custWarrantyCostModel1.premium)) {
                    premium = premium.add(new BigDecimal(custWarrantyCostModel1.premium));
                }

                if (StringKit.isNumeric(custWarrantyCostModel1.pay_money)) {
                    payMoney = payMoney.add(new BigDecimal(custWarrantyCostModel1.pay_money));
                }

                if (!isFindLast) {
                    isFindLast = !StringKit.isEmpty(custWarrantyCostModel1.actual_pay_time);
                }

                if (isFindLast || i == 0) {
                    warrantyStatusForPay = custWarrantyCostModel1.pay_status;
                    warrantyStatusForPayText = custWarrantyCostModel1.payStatusText(custWarrantyCostModel1.pay_status);
                }
            }

            response.data = new InsurancePolicy.GetInsurancePolicyDetail(insurancePolicyDetailByWarrantyCode, premium, payMoney, warrantyStatusForPay, warrantyStatusForPayText);
            response.data.insuredList = new ArrayList<>();
            response.data.beneficiaryList = new ArrayList<>();

            // TODO: 2018/4/17 真实的，目前不能用
            // InsuranceConciseInfo insuranceConciseInfo = insuranceServiceClient.insList(insurancePolicyDetailByWarrantyCode.product_id);

            if (insuranceParticipantByWarrantyCode != null && !insuranceParticipantByWarrantyCode.isEmpty()) {
//            response.data.insuranceProductName = insuranceConciseInfo.ins_name;
//            response.data.insuranceCompanyName = insuranceConciseInfo.company_name;
                response.data.insuranceProductName = "产品名";
                response.data.insuranceCompanyName = "保险公司名";

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

        custWarrantyCostModel.start_time = "0";
        custWarrantyCostModel.end_time = "0";

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

        if (StringKit.isEmpty(request.startTime)) {
            custWarrantyBrokerageModel.start_time = "0";
        } else {
            custWarrantyBrokerageModel.start_time = request.startTime;
        }

        if (StringKit.isEmpty(request.endTime)) {
            custWarrantyBrokerageModel.end_time = "0";
        } else {
            custWarrantyBrokerageModel.end_time = request.endTime;
        }

        if (StringKit.isEmpty(request.channelId)) {
            custWarrantyBrokerageModel.channel_id = "-1";
        } else {
            custWarrantyBrokerageModel.channel_id = request.channelId;
        }

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

        insurancePolicyModel1.start_time = "0";
        insurancePolicyModel1.end_time = "0";

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

            response.data.insurancePolicyCount = String.valueOf(count);
            response.data.premium = decimalFormat.format(premium.doubleValue());
            response.data.premiumText = "¥" + response.data.premium;

            response.data.brokerage = decimalFormat.format(brokerage.doubleValue());
            response.data.brokerageText = "¥" + response.data.brokerage;

            if (premium.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal divide = brokerage.divide(premium, BigDecimal.ROUND_HALF_DOWN);
                response.data.brokeragePercentage = String.valueOf(divide.doubleValue());
                divide = divide.multiply(new BigDecimal("100"));
                response.data.brokeragePercentageText = decimalFormat.format(divide.doubleValue()) + "%";
            } else {
                response.data.brokeragePercentage = "0.0";
                response.data.brokeragePercentageText = "0.00%";
            }

            if (map.size() != 0) {
                BigDecimal divide = premium.divide(new BigDecimal(response.data.insurancePolicyCount), BigDecimal.ROUND_HALF_DOWN);
                response.data.averagePremium = decimalFormat.format(divide.doubleValue());
                response.data.averagePremiumText = "¥" + response.data.averagePremium;
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
                    if (StringKit.equals(item.insurancePolicyCount, "0")) {
                        BigDecimal divide = itemPremium.divide(insurancePolicyCount, BigDecimal.ROUND_HALF_DOWN);
                        item.averagePremium = decimalFormat.format(divide.doubleValue());
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
                item.premiumText = "¥" + item.premium;
                item.averagePremium = decimalFormat.format(new BigDecimal(item.averagePremium).doubleValue());
                item.averagePremiumText = "¥" + item.averagePremium;
                item.brokerage = decimalFormat.format(itemBrokerage.doubleValue());
                item.brokerageText = "¥" + item.brokerage;

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


        if (StringKit.isEmpty(request.lastId)) {
            request.lastId = "0";
        }

        if (StringKit.isEmpty(request.pageNum)) {
            request.pageNum = "1";
        }

        if (StringKit.isEmpty(request.pageSize)) {
            request.pageSize = "20";
        }
        custWarrantyCostModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<BrokerageStatisticListModel> insurancePolicyBrokerageStatisticList = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticList(custWarrantyCostModel);
        long total = custWarrantyCostDao.findInsurancePolicyBrokerageStatisticListCount(custWarrantyCostModel);
        response.data = new ArrayList<>();
        long lastId = 0;

        if (insurancePolicyBrokerageStatisticList != null && !insurancePolicyBrokerageStatisticList.isEmpty()) {
            for (BrokerageStatisticListModel brokerageStatisticListModel : insurancePolicyBrokerageStatisticList) {
                response.data.add(new InsurancePolicy.InsurancePolicyBrokerageStatistic(brokerageStatisticListModel));
                lastId = Long.valueOf(brokerageStatisticListModel.cost_id);
            }
        }

        response.page = setPageBean(lastId,request.pageSize,total,response.data.size());

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);

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

}