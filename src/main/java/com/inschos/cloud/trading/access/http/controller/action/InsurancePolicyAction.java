package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.http.controller.bean.PaymentFinishBean;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.InsuranceClaimsDao;
import com.inschos.cloud.trading.data.dao.InsuranceParticipantDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.data.dao.InsurancePreservationDao;
import com.inschos.cloud.trading.model.InsuranceClaimsModel;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.InsurancePreservationModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 创建日期：2018/3/22 on 11:06
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicyAction extends BaseAction {

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    @Autowired
    private InsuranceParticipantDao insuranceParticipantDao;

    @Autowired
    private InsurancePreservationDao insurancePreservationDao;

    @Autowired
    private InsuranceClaimsDao insuranceClaimsDao;

    public String insure(InsurancePolicy.InsurancePolicyInsureForPersonRequest insurancePolicyInsureForPersonRequest) {

        // 校验有效性后，重新计算保费，用重新计算的保费写数据库，并生成订单。

        if (StringKit.isInteger(insurancePolicyInsureForPersonRequest.startTime) && StringKit.isInteger(insurancePolicyInsureForPersonRequest.endTime)) {
            if (Long.valueOf(insurancePolicyInsureForPersonRequest.endTime) <= Long.valueOf(insurancePolicyInsureForPersonRequest.startTime)) {
                return json(BaseResponse.CODE_FAILURE, "开始时间不能晚于结束时间", new BaseResponse());
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "开始时间与结束时间不正确", new BaseResponse());
        }

        long time = System.currentTimeMillis();

        // TODO: 2018/3/23 记得校验部分参数
        InsurancePolicyModel insurancePolicyBaseBean = new InsurancePolicyModel();

        insurancePolicyBaseBean.user_id = insurancePolicyInsureForPersonRequest.userId;
        insurancePolicyBaseBean.product_id = insurancePolicyInsureForPersonRequest.productId;
        insurancePolicyBaseBean.private_code = insurancePolicyBaseBean.createPrivateCode();

        insurancePolicyBaseBean.start_time = insurancePolicyInsureForPersonRequest.startTime;
        insurancePolicyBaseBean.end_time = insurancePolicyInsureForPersonRequest.endTime;
        insurancePolicyBaseBean.count = insurancePolicyInsureForPersonRequest.count;

        insurancePolicyBaseBean.created_at = String.valueOf(time);
        insurancePolicyBaseBean.updated_at = String.valueOf(time);

        InsuranceParticipantModel policyholder = new InsuranceParticipantModel();
        if (insurancePolicyInsureForPersonRequest.policyholder != null) {
            policyholder.type = InsuranceParticipantModel.TYPE_POLICYHOLDER;
            policyholder.name = insurancePolicyInsureForPersonRequest.policyholder.name;
            policyholder.card_type = insurancePolicyInsureForPersonRequest.policyholder.cardType;
            if (!policyholder.setCardCode(insurancePolicyInsureForPersonRequest.policyholder.cardCode)) {
                // 证件号码不符合规则
                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
            }
            policyholder.phone = insurancePolicyInsureForPersonRequest.policyholder.phone;

        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少投保人信息", new BaseResponse());
        }

        InsuranceParticipantModel insured = new InsuranceParticipantModel();

        if (insurancePolicyInsureForPersonRequest.insured != null) {
            policyholder.type = InsuranceParticipantModel.TYPE_INSURED;
            insured.relation_name = insurancePolicyInsureForPersonRequest.insured.relationName;
            insured.name = insurancePolicyInsureForPersonRequest.insured.name;
            insured.card_type = insurancePolicyInsureForPersonRequest.insured.cardType;
            if (!insured.setCardCode(insurancePolicyInsureForPersonRequest.insured.cardCode)) {
                // 证件号码不符合规则
                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
            }
            insured.phone = insurancePolicyInsureForPersonRequest.insured.phone;
            insured.birthday = insurancePolicyInsureForPersonRequest.insured.birthday;
            insured.sex = insurancePolicyInsureForPersonRequest.insured.sex;
            insured.phone = insurancePolicyInsureForPersonRequest.insured.phone;
            insured.occupation = insurancePolicyInsureForPersonRequest.insured.occupation;
            insured.email = insurancePolicyInsureForPersonRequest.insured.email;
            insured.area = insurancePolicyInsureForPersonRequest.insured.area;
            insured.address = insurancePolicyInsureForPersonRequest.insured.address;
            insured.nationality = insurancePolicyInsureForPersonRequest.insured.nationality;
            insured.annual_income = insurancePolicyInsureForPersonRequest.insured.annualIncome;
            insured.height = insurancePolicyInsureForPersonRequest.insured.height;
            insured.weight = insurancePolicyInsureForPersonRequest.insured.weight;

        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少被保险人信息", new BaseResponse());
        }

        InsuranceParticipantModel beneficiary = new InsuranceParticipantModel();
        if (insurancePolicyInsureForPersonRequest.beneficiary != null) {
            policyholder.type = InsuranceParticipantModel.TYPE_BENEFICIARY;
            beneficiary.relation_name = insurancePolicyInsureForPersonRequest.beneficiary.relationName;
            beneficiary.name = insurancePolicyInsureForPersonRequest.beneficiary.name;
            beneficiary.card_type = insurancePolicyInsureForPersonRequest.beneficiary.cardType;
            if (!beneficiary.setCardCode(insurancePolicyInsureForPersonRequest.beneficiary.cardCode)) {
                // 证件号码不符合规则
                return json(BaseResponse.CODE_FAILURE, "身份证号码不合法", new BaseResponse());
            }
            beneficiary.phone = insurancePolicyInsureForPersonRequest.beneficiary.phone;
            beneficiary.birthday = insurancePolicyInsureForPersonRequest.beneficiary.birthday;
            beneficiary.sex = insurancePolicyInsureForPersonRequest.beneficiary.sex;
            beneficiary.phone = insurancePolicyInsureForPersonRequest.beneficiary.phone;
            beneficiary.email = insurancePolicyInsureForPersonRequest.beneficiary.email;
            beneficiary.area = insurancePolicyInsureForPersonRequest.beneficiary.area;
            beneficiary.address = insurancePolicyInsureForPersonRequest.beneficiary.address;
            beneficiary.nationality = insurancePolicyInsureForPersonRequest.beneficiary.nationality;

        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少受益人信息", new BaseResponse());
        }


        // TODO: 2018/3/23 重新计算费用

        // TODO: 2018/3/28 补齐保单信息，添加保单
        int add = insurancePolicyDao.addInsurancePolicy(insurancePolicyBaseBean);
        if (add > 0) {

        } else {
            // TODO: 2018/3/28 回滚
        }

        // TODO: 2018/3/28 添加保单人员
        add = insuranceParticipantDao.addInsuranceParticipant(policyholder);
        if (add > 0) {

        } else {
            // TODO: 2018/3/28 回滚
        }

        add = insuranceParticipantDao.addInsuranceParticipant(insured);
        if (add > 0) {

        } else {
            // TODO: 2018/3/28 回滚
        }

        add = insuranceParticipantDao.addInsuranceParticipant(beneficiary);
        if (add > 0) {

        } else {
            // TODO: 2018/3/28 回滚
        }


        // TODO: 2018/3/23 成功后记得存保全记录
        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();

        insurancePreservationModel.cust_id = insurancePolicyInsureForPersonRequest.userId;
        insurancePreservationModel.private_code = insurancePolicyBaseBean.private_code;
        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_INSURE);
        insurancePreservationModel.apply_time = String.valueOf(time);
        insurancePreservationModel.created_at = String.valueOf(time);

        add = insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);

        if (add > 0) {

        } else {
            // TODO: 2018/3/28 回滚
        }

        return "";
    }

    public String surrender(InsurancePolicy.InsurancePolicySurrenderRequest insurancePolicySurrenderRequest) {

        // TODO: 2018/3/23 请求退保

        long time = System.currentTimeMillis();
        // TODO: 2018/3/23 成功后记得存保全记录
        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();


        insurancePreservationModel.cust_id = insurancePolicySurrenderRequest.userId;
        insurancePreservationModel.private_code = insurancePolicySurrenderRequest.privateCode;

        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_SURRENDER);
        insurancePreservationModel.apply_time = String.valueOf(time);
        insurancePreservationModel.created_at = String.valueOf(time);

        insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);

        return "";
    }

    public String findInsurancePolicyListByUserIdAndStatus(InsurancePolicy.InsurancePolicyListByUserIdAndStatusRequest insurancePolicyListByUserIdAndStatusRequest) {

        // 状态0为全部查   String userId, int status


        return "";
    }

    public String findInsurancePolicyListByOtherInfo(InsurancePolicy.InsurancePolicyListByOtherInfoRequest otherInfo) {

        // 状态0为全部查


        return "";
    }

    public String findInsurancePolicyDetailByPrivateCode(InsurancePolicy.InsurancePolicyDetailRequest insurancePolicyDetailRequest) {

        // int type, String privateCode
        // TODO: 2018/3/22 判断用户类型
        // TODO: 2018/3/22 需要一个别的服务的api

        InsurancePolicyModel insurancePolicyModel = insurancePolicyDao.findInsurancePolicyDetailByPrivateCode(insurancePolicyDetailRequest.privateCode);
        // 企业


        // 个人
        List<InsuranceParticipantModel> insuranceParticipantByPrivateCode = insuranceParticipantDao.findInsuranceParticipantByPrivateCode(insurancePolicyModel.private_code);

        return "";
    }

    public String findInsuranceClaimsListByUserId(InsurancePolicy.InsuranceClaimsListByUserIdRequest insuranceClaimsListByUserIdRequest) {

        InsuranceClaimsModel insuranceClaimsModel = new InsuranceClaimsModel();
        insuranceClaimsModel.user_id = insuranceClaimsListByUserIdRequest.userId;
        insuranceClaimsModel.status = insuranceClaimsListByUserIdRequest.status;
        insuranceClaimsModel.search = insuranceClaimsListByUserIdRequest.searchKey;


        List<InsuranceClaimsModel> insuranceClaimsListByUserId = insuranceClaimsDao.findInsuranceClaimsListByUserId(insuranceClaimsModel);

        insuranceClaimsListByUserId.sort((o1, o2) -> (int) (Long.valueOf(o1.created_at) - Long.valueOf(o2.created_at)));

        // TODO: 2018/3/27 保全记录

        return "";
    }


    /**
     * TODO 保费计算
     */
    private void calculatePremium() {

    }

    /**
     * TODO 获取健康告知
     */
    private void getHealthInform() {

    }

    /**
     * TODO 提交健康须知
     */
    private void commitHealthInform() {

    }

    /**
     * TODO 获取支付信息
     */
    private void getPaymentInform() {

    }

    /**
     * TODO 支付回调
     */
    private void onPaymentFinish (PaymentFinishBean paymentFinishBean) {

        if (StringKit.equals(paymentFinishBean.notice_type,"pay_call_back")) {

        }

    }

    /**
     * TODO 获取保单
     */
    private void getInsurancePolicy() {

    }

}
