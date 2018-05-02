package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
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
            insurancePolicyModel.channel_id = "-1";
        } else {
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

        return json(BaseResponse.CODE_SUCCESS, "获取保单详情成功", response);
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