package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.rpc.client.AccountClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.CarInfoDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.InsuranceParticipantDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CarInfoModel;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
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
            if (StringKit.isEmpty(request.warrantyStatus)) {
                insurancePolicyModel.status_string = "";
            } else {

            }
            insurancePolicyModel.warranty_status = StringKit.isEmpty(request.warrantyStatus) ? "0" : request.warrantyStatus;

            long total = insurancePolicyDao.findInsurancePolicyCountByWarrantyStatus(insurancePolicyModel);

            List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusOrSearch(insurancePolicyModel);
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

                        if (isFindLast) {
                            warrantyStatusForPay = custWarrantyCostModel1.pay_status;
                            warrantyStatusForPayText = custWarrantyCostModel1.payStatusText(custWarrantyCostModel1.pay_status);
                        }
                    }

                    InsurancePolicy.GetInsurancePolicy insurancePolicy = new InsurancePolicy.GetInsurancePolicy(policyListByWarrantyStatusOrSearch, premium, payMoney, warrantyStatusForPay, warrantyStatusForPayText);

                    if (StringKit.equals(insurancePolicy.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                        insurancePolicy.bjCodeFlag = carInfoDao.findBjCodeFlagByWarrantyUuid(insurancePolicy.warrantyUuid);
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

                if (isFindLast) {
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

            if (isFindLast) {
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

        String str;
        if (insurancePolicyDetailByWarrantyCode != null) {
            if (StringKit.equals(insurancePolicyDetailByWarrantyCode.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                // 车险
                CarInfoModel oneByWarrantyCode = carInfoDao.findOneByWarrantyUuid(warrantyUuid);
                response.data.carInfo = new InsurancePolicy.CarInfo(oneByWarrantyCode);
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

    public String getInsurancePolicyPayMoneyStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse();

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

        response.data = new InsurancePolicy.InsurancePolicyStatistic();

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();

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

        if (StringKit.isEmpty(request.channelId)) {
            insurancePolicyModel.channel_id = "-1";
        } else {
            insurancePolicyModel.channel_id = request.channelId;
        }

        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<InsurancePolicyModel> insurancePolicyListBySearchOrTimeOrChannelId = insurancePolicyDao.findInsurancePolicyListBySearchOrTimeOrChannelId(insurancePolicyModel);
        long total = insurancePolicyDao.findInsurancePolicyListBySearchOrTimeOrChannelIdCount(insurancePolicyModel);

        Calendar instance = Calendar.getInstance();

        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);


        InsurancePolicyModel insurancePolicyModel1 = new InsurancePolicyModel();
        insurancePolicyModel.manager_uuid = actionBean.managerUuid;

        instance.set(year, month, day, 0, 0, 0);
        insurancePolicyModel1.start_time = String.valueOf(instance.getTimeInMillis());

        int actualMaximum = instance.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (day < actualMaximum) {
            instance.set(year, month, day + 1, 0, 0, 0);
        } else {
            if (month + 1 < 12) {
                instance.set(year, month + 1, 1, 0, 0, 0);
            } else {
                instance.set(year + 1, 0, 1, 0, 0, 0);
            }
        }
        insurancePolicyModel1.end_time = String.valueOf(instance.getTimeInMillis());

        // 当前的所有付款的
//        Double dayAmount = insurancePolicyDao.findInsurancePolicyPayMoneyCountBySearchOrTimeOrChannelIdForManagerSystem(insurancePolicyModel1);
        Double dayAmount = null;

        if (dayAmount == null) {
            dayAmount = 0.00;
        }

        instance.set(year, month, 1, 0, 0, 0);
        insurancePolicyModel1.start_time = String.valueOf(instance.getTimeInMillis());

        if (month + 1 < 12) {
            instance.set(year, month + 1, 1, 0, 0, 0);
        } else {
            instance.set(year + 1, 0, 1, 0, 0, 0);
        }

        insurancePolicyModel1.end_time = String.valueOf(instance.getTimeInMillis());

        // 当月的所有付款的
//        Double monthAmount = insurancePolicyDao.findInsurancePolicyPayMoneyCountBySearchOrTimeOrChannelIdForManagerSystem(insurancePolicyModel1);
        Double monthAmount = null;

        if (monthAmount == null) {
            monthAmount = 0.00;
        }

        insurancePolicyModel1.start_time = "0";
        insurancePolicyModel1.end_time = "0";

//        Double totalAmount = insurancePolicyDao.findInsurancePolicyPayMoneyCountBySearchOrTimeOrChannelIdForManagerSystem(insurancePolicyModel1);
        Double totalAmount = null;

        if (totalAmount == null) {
            totalAmount = 0.00;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        response.data.dayAmount = "本日保费\n" + decimalFormat.format(dayAmount);
        response.data.monthAmount = "本月保费\n" + decimalFormat.format(monthAmount);
        response.data.totalAmount = "累计保费\n" + decimalFormat.format(totalAmount);
        response.data.list = new ArrayList<>();

//        if (insurancePolicyListBySearchOrTimeOrChannelId != null && !insurancePolicyListBySearchOrTimeOrChannelId.isEmpty()) {
//            for (InsurancePolicyModel model : insurancePolicyListBySearchOrTimeOrChannelId) {
//                response.data.list.add(new InsurancePolicy.GetInsurancePolicy(model));
//            }
//        }

        response.page = setPageBean(request.pageNum, request.pageSize, total, response.data.list.size());

        return json(BaseResponse.CODE_SUCCESS, "获取保单统计成功", response);
    }

    public String getInsurancePolicyBrokerageStatisticForManagerSystem(ActionBean actionBean) {
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest request = JsonKit.json2Bean(actionBean.body, InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemRequest.class);
        InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse response = new InsurancePolicy.GetInsurancePolicyStatisticForManagerSystemResponse();

//        if (request == null) {
//            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
//        }
//
//        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
//        if (entries != null) {
//            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
//        }
//
//        if (StringKit.isEmpty(request.lastId)) {
//            request.lastId = "0";
//        }
//
//        if (StringKit.isEmpty(request.pageNum)) {
//            request.pageNum = "1";
//        }
//
//        if (StringKit.isEmpty(request.pageSize)) {
//            request.pageSize = "10";
//        }
//
//        response.data = new InsurancePolicy.InsurancePolicyStatistic();
//
//        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
//
//        insurancePolicyModel.search = request.searchKey;
//
//        if (StringKit.isEmpty(request.startTime)) {
//            insurancePolicyModel.start_time = "0";
//        } else {
//            insurancePolicyModel.start_time = request.startTime;
//        }
//
//        if (StringKit.isEmpty(request.endTime)) {
//            insurancePolicyModel.end_time = "0";
//        } else {
//            insurancePolicyModel.end_time = request.endTime;
//        }
//
//        if (StringKit.isEmpty(request.channelId)) {
//            insurancePolicyModel.channel_id = "-1";
//        } else {
//            insurancePolicyModel.channel_id = request.channelId;
//        }
//
//        insurancePolicyModel.manager_uuid = actionBean.managerUuid;
//
//        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);
//
//        List<InsurancePolicyModel> insurancePolicyListBySearchOrTimeOrChannelId = insurancePolicyDao.findInsurancePolicyListBySearchOrTimeOrChannelId(insurancePolicyModel);
//        long total = insurancePolicyDao.findInsurancePolicyListBySearchOrTimeOrChannelIdCount(insurancePolicyModel);
//
//        Calendar instance = Calendar.getInstance();
//
//        int year = instance.get(Calendar.YEAR);
//        int month = instance.get(Calendar.MONTH);
//        int day = instance.get(Calendar.DAY_OF_MONTH);
//
//
//        InsurancePolicyModel insurancePolicyModel1 = new InsurancePolicyModel();
//        insurancePolicyModel.manager_uuid = actionBean.managerUuid;
//
//        instance.set(year, month, day, 0, 0, 0);
//        insurancePolicyModel1.start_time = String.valueOf(instance.getTimeInMillis());
//
//        int actualMaximum = instance.getActualMaximum(Calendar.DAY_OF_MONTH);
//        if (day < actualMaximum) {
//            instance.set(year, month, day + 1, 0, 0, 0);
//        } else {
//            if (month + 1 < 12) {
//                instance.set(year, month + 1, 1, 0, 0, 0);
//            } else {
//                instance.set(year + 1, 0, 1, 0, 0, 0);
//            }
//        }
//        insurancePolicyModel1.end_time = String.valueOf(instance.getTimeInMillis());
//
//        // 当前的所有付款的
//        Double dayAmount = insurancePolicyDao.(insurancePolicyModel1);
//
//        if (dayAmount == null) {
//            dayAmount = 0.00;
//        }
//
//        instance.set(year, month, 1, 0, 0, 0);
//        insurancePolicyModel1.start_time = String.valueOf(instance.getTimeInMillis());
//
//        if (month + 1 < 12) {
//            instance.set(year, month + 1, 1, 0, 0, 0);
//        } else {
//            instance.set(year + 1, 0, 1, 0, 0, 0);
//        }
//
//        insurancePolicyModel1.end_time = String.valueOf(instance.getTimeInMillis());
//
//        // 当月的所有付款的
//        Double monthAmount = insurancePolicyDao.(insurancePolicyModel1);
//
//        if (monthAmount == null) {
//            monthAmount = 0.00;
//        }
//
//        insurancePolicyModel1.start_time = "0";
//        insurancePolicyModel1.end_time = "0";
//
//        Double totalAmount = insurancePolicyDao.(insurancePolicyModel1);
//
//        if (totalAmount == null) {
//            totalAmount = 0.00;
//        }
//
//        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//
//        response.data.dayAmount = "本日保费\n" + decimalFormat.format(dayAmount);
//        response.data.monthAmount = "本月保费\n" + decimalFormat.format(monthAmount);
//        response.data.totalAmount = "累计保费\n" + decimalFormat.format(totalAmount);
//        response.data.list = new ArrayList<>();
//
//        if (insurancePolicyListBySearchOrTimeOrChannelId != null && !insurancePolicyListBySearchOrTimeOrChannelId.isEmpty()) {
//            for (InsurancePolicyModel model : insurancePolicyListBySearchOrTimeOrChannelId) {
//                response.data.list.add(new InsurancePolicy.GetInsurancePolicy(model));
//            }
//        }
//
//        response.page = setPageBean(request.pageNum, request.pageSize, total, response.data.list.size());

        return json(BaseResponse.CODE_SUCCESS, "获取佣金统计成功", response);
    }


//    public String insure(InsurancePolicy.InsurancePolicyInsureForPersonRequest insurancePolicyInsureForPersonRequest) {
//
//        // 校验有效性后，重新计算保费，用重新计算的保费写数据库，并生成订单。
//
//        if (StringKit.isInteger(insurancePolicyInsureForPersonRequest.startTime) && StringKit.isInteger(insurancePolicyInsureForPersonRequest.endTime)) {
//            if (Long.valueOf(insurancePolicyInsureForPersonRequest.endTime) <= Long.valueOf(insurancePolicyInsureForPersonRequest.startTime)) {
//                return json(BaseResponse.CODE_FAILURE, "开始时间不能晚于结束时间", new BaseResponse());
//            }
//        } else {
//            return json(BaseResponse.CODE_FAILURE, "开始时间与结束时间不正确", new BaseResponse());
//        }
//
//        long time = System.currentTimeMillis();
//
//        // TODO: 2018/3/23 记得校验部分参数
//        InsurancePolicyModel insurancePolicyBaseBean = new InsurancePolicyModel();
//
//        insurancePolicyBaseBean.user_id = insurancePolicyInsureForPersonRequest.userId;
//        insurancePolicyBaseBean.product_id = insurancePolicyInsureForPersonRequest.productId;
//        insurancePolicyBaseBean.warranty_uuid = insurancePolicyBaseBean.createPrivateCode();
//
//        insurancePolicyBaseBean.start_time = insurancePolicyInsureForPersonRequest.startTime;
//        insurancePolicyBaseBean.end_time = insurancePolicyInsureForPersonRequest.endTime;
//        insurancePolicyBaseBean.count = insurancePolicyInsureForPersonRequest.count;
//
//        insurancePolicyBaseBean.created_at = String.valueOf(time);
//        insurancePolicyBaseBean.updated_at = String.valueOf(time);
//
//        InsuranceParticipantModel policyholder = new InsuranceParticipantModel();
//        if (insurancePolicyInsureForPersonRequest.policyholder != null) {
//            policyholder.type = InsuranceParticipantModel.TYPE_POLICYHOLDER;
//            policyholder.name = insurancePolicyInsureForPersonRequest.policyholder.name;
//            policyholder.card_type = insurancePolicyInsureForPersonRequest.policyholder.cardType;
//            if (!policyholder.setCardCode(insurancePolicyInsureForPersonRequest.policyholder.cardCode)) {
//                // 证件号码不符合规则
//                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
//            }
//            policyholder.phone = insurancePolicyInsureForPersonRequest.policyholder.phone;
//
//        } else {
//            return json(BaseResponse.CODE_FAILURE, "缺少投保人信息", new BaseResponse());
//        }
//
//        InsuranceParticipantModel insured = new InsuranceParticipantModel();
//
//        if (insurancePolicyInsureForPersonRequest.insured != null) {
//            policyholder.type = InsuranceParticipantModel.TYPE_INSURED;
//            insured.relation_name = insurancePolicyInsureForPersonRequest.insured.relationName;
//            insured.name = insurancePolicyInsureForPersonRequest.insured.name;
//            insured.card_type = insurancePolicyInsureForPersonRequest.insured.cardType;
//            if (!insured.setCardCode(insurancePolicyInsureForPersonRequest.insured.cardCode)) {
//                // 证件号码不符合规则
//                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
//            }
//            insured.phone = insurancePolicyInsureForPersonRequest.insured.phone;
//            insured.birthday = insurancePolicyInsureForPersonRequest.insured.birthday;
//            insured.sex = insurancePolicyInsureForPersonRequest.insured.sex;
//            insured.phone = insurancePolicyInsureForPersonRequest.insured.phone;
//            insured.occupation = insurancePolicyInsureForPersonRequest.insured.occupation;
//            insured.email = insurancePolicyInsureForPersonRequest.insured.email;
//            insured.area = insurancePolicyInsureForPersonRequest.insured.area;
//            insured.address = insurancePolicyInsureForPersonRequest.insured.address;
//            insured.nationality = insurancePolicyInsureForPersonRequest.insured.nationality;
//            insured.annual_income = insurancePolicyInsureForPersonRequest.insured.annualIncome;
//            insured.height = insurancePolicyInsureForPersonRequest.insured.height;
//            insured.weight = insurancePolicyInsureForPersonRequest.insured.weight;
//
//        } else {
//            return json(BaseResponse.CODE_FAILURE, "缺少被保险人信息", new BaseResponse());
//        }
//
//        InsuranceParticipantModel beneficiary = new InsuranceParticipantModel();
//        if (insurancePolicyInsureForPersonRequest.beneficiary != null) {
//            policyholder.type = InsuranceParticipantModel.TYPE_BENEFICIARY;
//            beneficiary.relation_name = insurancePolicyInsureForPersonRequest.beneficiary.relationName;
//            beneficiary.name = insurancePolicyInsureForPersonRequest.beneficiary.name;
//            beneficiary.card_type = insurancePolicyInsureForPersonRequest.beneficiary.cardType;
//            if (!beneficiary.setCardCode(insurancePolicyInsureForPersonRequest.beneficiary.cardCode)) {
//                // 证件号码不符合规则
//                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
//            }
//            beneficiary.phone = insurancePolicyInsureForPersonRequest.beneficiary.phone;
//            beneficiary.birthday = insurancePolicyInsureForPersonRequest.beneficiary.birthday;
//            beneficiary.sex = insurancePolicyInsureForPersonRequest.beneficiary.sex;
//            beneficiary.phone = insurancePolicyInsureForPersonRequest.beneficiary.phone;
//            beneficiary.email = insurancePolicyInsureForPersonRequest.beneficiary.email;
//            beneficiary.area = insurancePolicyInsureForPersonRequest.beneficiary.area;
//            beneficiary.address = insurancePolicyInsureForPersonRequest.beneficiary.address;
//            beneficiary.nationality = insurancePolicyInsureForPersonRequest.beneficiary.nationality;
//
//        } else {
//            return json(BaseResponse.CODE_FAILURE, "缺少受益人信息", new BaseResponse());
//        }
//
//
//        // TODO: 2018/3/23 重新计算费用
//
//        // TODO: 2018/3/28 补齐保单信息，添加保单
//        int add = insurancePolicyDao.addInsurancePolicy(insurancePolicyBaseBean);
//        if (add > 0) {
//
//        } else {
//            // TODO: 2018/3/28 回滚
//        }
//
//        // TODO: 2018/3/28 添加保单人员
//        add = insuranceParticipantDao.addInsuranceParticipant(policyholder);
//        if (add > 0) {
//
//        } else {
//            // TODO: 2018/3/28 回滚
//        }
//
//        add = insuranceParticipantDao.addInsuranceParticipant(insured);
//        if (add > 0) {
//
//        } else {
//            // TODO: 2018/3/28 回滚
//        }
//
//        add = insuranceParticipantDao.addInsuranceParticipant(beneficiary);
//        if (add > 0) {
//
//        } else {
//            // TODO: 2018/3/28 回滚
//        }
//
//
//        // TODO: 2018/3/23 成功后记得存保全记录
//        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();
//
//        insurancePreservationModel.cust_id = insurancePolicyInsureForPersonRequest.userId;
//        insurancePreservationModel.private_code = insurancePolicyBaseBean.warranty_uuid;
//        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_INSURE);
//        insurancePreservationModel.apply_time = String.valueOf(time);
//        insurancePreservationModel.created_at = String.valueOf(time);
//
//        add = insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);
//
//        if (add > 0) {
//
//        } else {
//            // TODO: 2018/3/28 回滚
//        }
//
//        return "";
//    }
//
//    public String surrender(InsurancePolicy.InsurancePolicySurrenderRequest insurancePolicySurrenderRequest) {
//
//        // TODO: 2018/3/23 请求退保
//
//        long time = System.currentTimeMillis();
//        // TODO: 2018/3/23 成功后记得存保全记录
//        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();
//
//
//        insurancePreservationModel.cust_id = insurancePolicySurrenderRequest.userId;
//        insurancePreservationModel.private_code = insurancePolicySurrenderRequest.privateCode;
//
//        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_SURRENDER);
//        insurancePreservationModel.apply_time = String.valueOf(time);
//        insurancePreservationModel.created_at = String.valueOf(time);
//
//        insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);
//
//        return "";
//    }
//
//    public String findInsurancePolicyListByUserIdAndStatus(InsurancePolicy.InsurancePolicyListByUserIdAndStatusRequest insurancePolicyListByUserIdAndStatusRequest) {
//
//        // 状态0为全部查   String userId, int status
//
//
//        return "";
//    }
//
//    public String findInsurancePolicyListByOtherInfo(InsurancePolicy.InsurancePolicyListByOtherInfoRequest otherInfo) {
//
//        // 状态0为全部查
//
//
//        return "";
//    }
//
//    public String findInsurancePolicyDetailByPrivateCode(InsurancePolicy.InsurancePolicyDetailRequest insurancePolicyDetailRequest) {
//
//        // int type, String privateCode
//        // TODO: 2018/3/22 判断用户类型
//        // TODO: 2018/3/22 需要一个别的服务的api
//
//        InsurancePolicyModel insurancePolicyModel = insurancePolicyDao.findInsurancePolicyDetailByPrivateCode(insurancePolicyDetailRequest.privateCode);
//        // 企业
//
//
//
//        // 个人
//        List<InsuranceParticipantModel> insuranceParticipantByPrivateCode = insuranceParticipantDao.findInsuranceParticipantByPrivateCode(insurancePolicyModel.warranty_uuid);
//
//        return "";
//    }
//
//    public String findInsuranceClaimsListByUserId(InsurancePolicy.InsuranceClaimsListByUserIdRequest insuranceClaimsListByUserIdRequest) {
//
//        InsuranceClaimsModel insuranceClaimsModel = new InsuranceClaimsModel();
//        insuranceClaimsModel.user_id = insuranceClaimsListByUserIdRequest.userId;
//        insuranceClaimsModel.status = insuranceClaimsListByUserIdRequest.value;
//        insuranceClaimsModel.search = insuranceClaimsListByUserIdRequest.searchKey;
//
//
//        List<InsuranceClaimsModel> insuranceClaimsListByUserId = insuranceClaimsDao.findInsuranceClaimsListByUserId(insuranceClaimsModel);
//
//        insuranceClaimsListByUserId.sort((o1, o2) -> (int) (Long.valueOf(o1.created_at) - Long.valueOf(o2.created_at)));
//
//        // TODO: 2018/3/27 保全记录
//
//        return "";
//    }
//
//
//    /**
//     * TODO 保费计算
//     */
//    private void calculatePremium() {
//
//    }
//
//    /**
//     * TODO 获取健康告知
//     */
//    private void getHealthInform() {
//
//    }
//
//    /**
//     * TODO 提交健康须知
//     */
//    private void commitHealthInform() {
//
//    }
//
//    /**
//     * TODO 获取支付信息
//     */
//    private void getPaymentInform() {
//
//    }
//
//    /**
//     * TODO 支付回调
//     */
//    private void onPaymentFinish(PaymentFinishBean paymentFinishBean) {
//        if (StringKit.equals(paymentFinishBean.notice_type, "pay_call_back")) {
//            if (paymentFinishBean.data != null && !StringKit.isEmpty(paymentFinishBean.data.union_order_code) && paymentFinishBean.data.status) {
//
//                String privateCode = insurancePolicyDao.findInsurancePolicyPrivateCodeByUnionOrderCode(paymentFinishBean.data.union_order_code);
//
//                if (!StringKit.isEmpty(privateCode)) {
//
//                    List<InsuranceParticipantModel> insuranceParticipantInsuredByPrivateCode = insuranceParticipantDao.findInsuranceParticipantInsuredByPrivateCode(privateCode);
//
//                    for (InsuranceParticipantModel insuranceParticipantModel : insuranceParticipantInsuredByPrivateCode) {
//                        InsurancePolicyCodeBean.InsurancePolicyCodeBeanRequest insurancePolicyCodeBeanRequest = new InsurancePolicyCodeBean.InsurancePolicyCodeBeanRequest();
//public String insure(InsurancePolicy.InsurancePolicyInsureForPersonRequest insurancePolicyInsureForPersonRequest) {
////
////        // 校验有效性后，重新计算保费，用重新计算的保费写数据库，并生成订单。
////
////        if (StringKit.isInteger(insurancePolicyInsureForPersonRequest.startTime) && StringKit.isInteger(insurancePolicyInsureForPersonRequest.endTime)) {
////            if (Long.valueOf(insurancePolicyInsureForPersonRequest.endTime) <= Long.valueOf(insurancePolicyInsureForPersonRequest.startTime)) {
////                return json(BaseResponse.CODE_FAILURE, "开始时间不能晚于结束时间", new BaseResponse());
////            }
////        } else {
////            return json(BaseResponse.CODE_FAILURE, "开始时间与结束时间不正确", new BaseResponse());
////        }
////
////        long time = System.currentTimeMillis();
////
////        // TODO: 2018/3/23 记得校验部分参数
////        InsurancePolicyModel insurancePolicyBaseBean = new InsurancePolicyModel();
////
////        insurancePolicyBaseBean.user_id = insurancePolicyInsureForPersonRequest.userId;
////        insurancePolicyBaseBean.product_id = insurancePolicyInsureForPersonRequest.productId;
////        insurancePolicyBaseBean.warranty_uuid = insurancePolicyBaseBean.createPrivateCode();
////
////        insurancePolicyBaseBean.start_time = insurancePolicyInsureForPersonRequest.startTime;
////        insurancePolicyBaseBean.end_time = insurancePolicyInsureForPersonRequest.endTime;
////        insurancePolicyBaseBean.count = insurancePolicyInsureForPersonRequest.count;
////
////        insurancePolicyBaseBean.created_at = String.valueOf(time);
////        insurancePolicyBaseBean.updated_at = String.valueOf(time);
////
////        InsuranceParticipantModel policyholder = new InsuranceParticipantModel();
////        if (insurancePolicyInsureForPersonRequest.policyholder != null) {
////            policyholder.type = InsuranceParticipantModel.TYPE_POLICYHOLDER;
////            policyholder.name = insurancePolicyInsureForPersonRequest.policyholder.name;
////            policyholder.card_type = insurancePolicyInsureForPersonRequest.policyholder.cardType;
////            if (!policyholder.setCardCode(insurancePolicyInsureForPersonRequest.policyholder.cardCode)) {
////                // 证件号码不符合规则
////                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
////            }
////            policyholder.phone = insurancePolicyInsureForPersonRequest.policyholder.phone;
////
////        } else {
////            return json(BaseResponse.CODE_FAILURE, "缺少投保人信息", new BaseResponse());
////        }
////
////        InsuranceParticipantModel insured = new InsuranceParticipantModel();
////
////        if (insurancePolicyInsureForPersonRequest.insured != null) {
////            policyholder.type = InsuranceParticipantModel.TYPE_INSURED;
////            insured.relation_name = insurancePolicyInsureForPersonRequest.insured.relationName;
////            insured.name = insurancePolicyInsureForPersonRequest.insured.name;
////            insured.card_type = insurancePolicyInsureForPersonRequest.insured.cardType;
////            if (!insured.setCardCode(insurancePolicyInsureForPersonRequest.insured.cardCode)) {
////                // 证件号码不符合规则
////                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
////            }
////            insured.phone = insurancePolicyInsureForPersonRequest.insured.phone;
////            insured.birthday = insurancePolicyInsureForPersonRequest.insured.birthday;
////            insured.sex = insurancePolicyInsureForPersonRequest.insured.sex;
////            insured.phone = insurancePolicyInsureForPersonRequest.insured.phone;
////            insured.occupation = insurancePolicyInsureForPersonRequest.insured.occupation;
////            insured.email = insurancePolicyInsureForPersonRequest.insured.email;
////            insured.area = insurancePolicyInsureForPersonRequest.insured.area;
////            insured.address = insurancePolicyInsureForPersonRequest.insured.address;
////            insured.nationality = insurancePolicyInsureForPersonRequest.insured.nationality;
////            insured.annual_income = insurancePolicyInsureForPersonRequest.insured.annualIncome;
////            insured.height = insurancePolicyInsureForPersonRequest.insured.height;
////            insured.weight = insurancePolicyInsureForPersonRequest.insured.weight;
////
////        } else {
////            return json(BaseResponse.CODE_FAILURE, "缺少被保险人信息", new BaseResponse());
////        }
////
////        InsuranceParticipantModel beneficiary = new InsuranceParticipantModel();
////        if (insurancePolicyInsureForPersonRequest.beneficiary != null) {
////            policyholder.type = InsuranceParticipantModel.TYPE_BENEFICIARY;
////            beneficiary.relation_name = insurancePolicyInsureForPersonRequest.beneficiary.relationName;
////            beneficiary.name = insurancePolicyInsureForPersonRequest.beneficiary.name;
////            beneficiary.card_type = insurancePolicyInsureForPersonRequest.beneficiary.cardType;
////            if (!beneficiary.setCardCode(insurancePolicyInsureForPersonRequest.beneficiary.cardCode)) {
////                // 证件号码不符合规则
////                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
////            }
////            beneficiary.phone = insurancePolicyInsureForPersonRequest.beneficiary.phone;
////            beneficiary.birthday = insurancePolicyInsureForPersonRequest.beneficiary.birthday;
////            beneficiary.sex = insurancePolicyInsureForPersonRequest.beneficiary.sex;
////            beneficiary.phone = insurancePolicyInsureForPersonRequest.beneficiary.phone;
////            beneficiary.email = insurancePolicyInsureForPersonRequest.beneficiary.email;
////            beneficiary.area = insurancePolicyInsureForPersonRequest.beneficiary.area;
////            beneficiary.address = insurancePolicyInsureForPersonRequest.beneficiary.address;
////            beneficiary.nationality = insurancePolicyInsureForPersonRequest.beneficiary.nationality;
////
////        } else {
////            return json(BaseResponse.CODE_FAILURE, "缺少受益人信息", new BaseResponse());
////        }
////
////
////        // TODO: 2018/3/23 重新计算费用
////
////        // TODO: 2018/3/28 补齐保单信息，添加保单
////        int add = insurancePolicyDao.addInsurancePolicy(insurancePolicyBaseBean);
////        if (add > 0) {
////
////        } else {
////            // TODO: 2018/3/28 回滚
////        }
////
////        // TODO: 2018/3/28 添加保单人员
////        add = insuranceParticipantDao.addInsuranceParticipant(policyholder);
////        if (add > 0) {
////
////        } else {
////            // TODO: 2018/3/28 回滚
////        }
////
////        add = insuranceParticipantDao.addInsuranceParticipant(insured);
////        if (add > 0) {
////
////        } else {
////            // TODO: 2018/3/28 回滚
////        }
////
////        add = insuranceParticipantDao.addInsuranceParticipant(beneficiary);
////        if (add > 0) {
////
////        } else {
////            // TODO: 2018/3/28 回滚
////        }
////
////
////        // TODO: 2018/3/23 成功后记得存保全记录
////        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();
////
////        insurancePreservationModel.cust_id = insurancePolicyInsureForPersonRequest.userId;
////        insurancePreservationModel.private_code = insurancePolicyBaseBean.warranty_uuid;
////        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_INSURE);
////        insurancePreservationModel.apply_time = String.valueOf(time);
////        insurancePreservationModel.created_at = String.valueOf(time);
////
////        add = insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);
////
////        if (add > 0) {
////
////        } else {
////            // TODO: 2018/3/28 回滚
////        }
////
////        return "";
////    }
////
////    public String surrender(InsurancePolicy.InsurancePolicySurrenderRequest insurancePolicySurrenderRequest) {
////
////        // TODO: 2018/3/23 请求退保
////
////        long time = System.currentTimeMillis();
////        // TODO: 2018/3/23 成功后记得存保全记录
////        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();
////
////
////        insurancePreservationModel.cust_id = insurancePolicySurrenderRequest.userId;
////        insurancePreservationModel.private_code = insurancePolicySurrenderRequest.privateCode;
////
////        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_SURRENDER);
////        insurancePreservationModel.apply_time = String.valueOf(time);
////        insurancePreservationModel.created_at = String.valueOf(time);
////
////        insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);
////
////        return "";
////    }
////
////    public String findInsurancePolicyListByUserIdAndStatus(InsurancePolicy.InsurancePolicyListByUserIdAndStatusRequest insurancePolicyListByUserIdAndStatusRequest) {
////
////        // 状态0为全部查   String userId, int status
////
////
////        return "";
////    }
////
////    public String findInsurancePolicyListByOtherInfo(InsurancePolicy.InsurancePolicyListByOtherInfoRequest otherInfo) {
////
////        // 状态0为全部查
////
////
////        return "";
////    }
////
////    public String findInsurancePolicyDetailByPrivateCode(InsurancePolicy.InsurancePolicyDetailRequest insurancePolicyDetailRequest) {
////
////        // int type, String privateCode
////        // TODO: 2018/3/22 判断用户类型
////        // TODO: 2018/3/22 需要一个别的服务的api
////
////        InsurancePolicyModel insurancePolicyModel = insurancePolicyDao.findInsurancePolicyDetailByPrivateCode(insurancePolicyDetailRequest.privateCode);
////        // 企业
////
////
////
////        // 个人
////        List<InsuranceParticipantModel> insuranceParticipantByPrivateCode = insuranceParticipantDao.findInsuranceParticipantByPrivateCode(insurancePolicyModel.warranty_uuid);
////
////        return "";
////    }
////
////    public String findInsuranceClaimsListByUserId(InsurancePolicy.InsuranceClaimsListByUserIdRequest insuranceClaimsListByUserIdRequest) {
////
////        InsuranceClaimsModel insuranceClaimsModel = new InsuranceClaimsModel();
////        insuranceClaimsModel.user_id = insuranceClaimsListByUserIdRequest.userId;
////        insuranceClaimsModel.status = insuranceClaimsListByUserIdRequest.value;
////        insuranceClaimsModel.search = insuranceClaimsListByUserIdRequest.searchKey;
////
////
////        List<InsuranceClaimsModel> insuranceClaimsListByUserId = insuranceClaimsDao.findInsuranceClaimsListByUserId(insuranceClaimsModel);
////
////        insuranceClaimsListByUserId.sort((o1, o2) -> (int) (Long.valueOf(o1.created_at) - Long.valueOf(o2.created_at)));
////
////        // TODO: 2018/3/27 保全记录
////
////        return "";
////    }
////
////
////    /**
////     * TODO 保费计算
////     */
////    private void calculatePremium() {
////
////    }
////
////    /**
////     * TODO 获取健康告知
////     */
////    private void getHealthInform() {
////
////    }
////
////    /**
////     * TODO 提交健康须知
////     */
////    private void commitHealthInform() {
////
////    }
////
////    /**
////     * TODO 获取支付信息
////     */
////    private void getPaymentInform() {
////
////    }
////
////    /**
////     * TODO 支付回调
////     */
////    private void onPaymentFinish(PaymentFinishBean paymentFinishBean) {
////        if (StringKit.equals(paymentFinishBean.notice_type, "pay_call_back")) {
////            if (paymentFinishBean.data != null && !StringKit.isEmpty(paymentFinishBean.data.union_order_code) && paymentFinishBean.data.status) {
////
////                String privateCode = insurancePolicyDao.findInsurancePolicyPrivateCodeByUnionOrderCode(paymentFinishBean.data.union_order_code);
////
////                if (!StringKit.isEmpty(privateCode)) {
////
////                    List<InsuranceParticipantModel> insuranceParticipantInsuredByPrivateCode = insuranceParticipantDao.findInsuranceParticipantInsuredByPrivateCode(privateCode);
////
////                    for (InsuranceParticipantModel insuranceParticipantModel : insuranceParticipantInsuredByPrivateCode) {
////                        InsurancePolicyCodeBean.InsurancePolicyCodeBeanRequest insurancePolicyCodeBeanRequest = new InsurancePolicyCodeBean.InsurancePolicyCodeBeanRequest();
////
////                        insurancePolicyCodeBeanRequest.order_code = insuranceParticipantModel.out_order_code;
////                        insurancePolicyCodeBeanRequest.union_order_code = paymentFinishBean.data.union_order_code;
////                        insurancePolicyCodeBeanRequest.private_p_code = privateCode;
////
////                        try {
////                            String result = HttpClientKit.post("", JsonKit.bean2Json(insurancePolicyCodeBeanRequest));
////
////                            InsurancePolicyCodeBean.InsurancePolicyCodeBeanResponse insurancePolicyCodeBeanResponse = JsonKit.json2Bean(result, InsurancePolicyCodeBean.InsurancePolicyCodeBeanResponse.class);
////
////                            // TODO: 2018/3/29 怎么存保单号
////                            // insurancePolicyCodeBeanResponse.policy_order_code
////
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                    }
////
////                }
////
////            }
////        }
////    }
////
////    /**
////     * TODO 获取保单
////     */
////    private void getInsurancePolicy() {
////
////    }
//                        insurancePolicyCodeBeanRequest.order_code = insuranceParticipantModel.out_order_code;
//                        insurancePolicyCodeBeanRequest.union_order_code = paymentFinishBean.data.union_order_code;
//                        insurancePolicyCodeBeanRequest.private_p_code = privateCode;
//
//                        try {
//                            String result = HttpClientKit.post("", JsonKit.bean2Json(insurancePolicyCodeBeanRequest));
//
//                            InsurancePolicyCodeBean.InsurancePolicyCodeBeanResponse insurancePolicyCodeBeanResponse = JsonKit.json2Bean(result, InsurancePolicyCodeBean.InsurancePolicyCodeBeanResponse.class);
//
//                            // TODO: 2018/3/29 怎么存保单号
//                            // insurancePolicyCodeBeanResponse.policy_order_code
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//
//            }
//        }
//    }
//
//    /**
//     * TODO 获取保单
//     */
//    private void getInsurancePolicy() {
//
//    }

}
