package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.CarInfoDao;
import com.inschos.cloud.trading.data.dao.InsuranceParticipantDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CarInfoModel;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
            getInsurancePolicyStatus.status = string;
            getInsurancePolicyStatus.statusText = warrantyStatusMap.get(string);
            response.data.add(getInsurancePolicyStatus);
        }

        return json(BaseResponse.CODE_SUCCESS, "获取保单状态分类成功", response);
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

        String str;

        if (!StringKit.isEmpty(request.searchKey) || !StringKit.isEmpty(request.warrantyStatus)) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();

            insurancePolicyModel.account_uuid = actionBean.accountUuid;
            insurancePolicyModel.warranty_status = StringKit.isEmpty(request.warrantyStatus) ? "0" : request.warrantyStatus;
            insurancePolicyModel.search = request.searchKey;
            insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

            long total = insurancePolicyDao.findInsurancePolicyCountByWarrantyStatus(insurancePolicyModel);

            List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusOrSearch(insurancePolicyModel);
            response.data = new ArrayList<>();

            if (insurancePolicyListByWarrantyStatusOrSearch != null && !insurancePolicyListByWarrantyStatusOrSearch.isEmpty()) {
                for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyListByWarrantyStatusOrSearch) {
                    InsurancePolicy.GetInsurancePolicy insurancePolicy = new InsurancePolicy.GetInsurancePolicy(policyListByWarrantyStatusOrSearch);
                    List<InsuranceParticipantModel> insuranceParticipantInsuredByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantInsuredNameByWarrantyUuid(insurancePolicy.warrantyUuid);

                    if (insuranceParticipantInsuredByWarrantyUuid.isEmpty()) {
                        insurancePolicy.insuredText = "";
                    } else {
                        InsuranceParticipantModel insuranceParticipantModel = insuranceParticipantInsuredByWarrantyUuid.get(0);
                        insurancePolicy.insuredText = insuranceParticipantModel.name + "等" + insuranceParticipantInsuredByWarrantyUuid.size() + "人";
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

        InsurancePolicyModel insurancePolicyDetailByWarrantyCode = insurancePolicyDao.findInsurancePolicyDetailByWarrantyCode(request.warrantyUuid);
        List<InsuranceParticipantModel> insuranceParticipantByWarrantyCode = insuranceParticipantDao.findInsuranceParticipantByWarrantyUuid(request.warrantyUuid);

        response.data = new InsurancePolicy.GetInsurancePolicyDetail(insurancePolicyDetailByWarrantyCode);
        response.data.insuredList = new ArrayList<>();
        response.data.beneficiaryList = new ArrayList<>();

        if (insuranceParticipantByWarrantyCode != null && !insuranceParticipantByWarrantyCode.isEmpty()) {
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
                CarInfoModel oneByWarrantyCode = carInfoDao.findOneByWarrantyUuid(request.warrantyUuid);
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

        if (StringKit.isEmpty(request.ditchId)) {
            insurancePolicyModel.ditch_id = "-1";
        } else {
            insurancePolicyModel.ditch_id = request.ditchId;
        }

        insurancePolicyModel.manager_uuid = "2";

        insurancePolicyModel.page = setPage(request.lastId, request.pageNum, request.pageSize);

        List<InsurancePolicyModel> insurancePolicyListByWarrantyStatusOrSearch = insurancePolicyDao.findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrDitchId(insurancePolicyModel);
        response.data = new ArrayList<>();

        for (InsurancePolicyModel policyListByWarrantyStatusOrSearch : insurancePolicyListByWarrantyStatusOrSearch) {
            InsurancePolicy.GetInsurancePolicyForManagerSystem getInsurancePolicyForManagerSystem = new InsurancePolicy.GetInsurancePolicyForManagerSystem(policyListByWarrantyStatusOrSearch);
            if (StringKit.equals(policyListByWarrantyStatusOrSearch.type, InsurancePolicyModel.POLICY_TYPE_CAR)) {
                InsuranceParticipantModel insuranceParticipantPolicyHolderNameByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantPolicyHolderNameByWarrantyUuid(policyListByWarrantyStatusOrSearch.warranty_uuid);
                if (insuranceParticipantPolicyHolderNameByWarrantyUuid != null) {
                    getInsurancePolicyForManagerSystem.policyHolderName = insuranceParticipantPolicyHolderNameByWarrantyUuid.name;
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

        return json(BaseResponse.CODE_SUCCESS, "获取保单详情成功", response);
    }

    public String getInsurancePolicyDetailForManagerSystem(ActionBean actionBean) {

        return null;
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
//        insuranceClaimsModel.status = insuranceClaimsListByUserIdRequest.status;
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
////        insuranceClaimsModel.status = insuranceClaimsListByUserIdRequest.status;
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
