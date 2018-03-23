package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.InsuranceParticipantDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.data.dao.InsurancePreservationDao;
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

    public String insure(InsurancePolicy.InsurancePolicyInsureForPersonRequest insurancePolicyInsureForPersonRequest) {

        // 校验有效性后，重新计算保费，用重新计算的保费写数据库，并生成订单。

        if (StringKit.isInteger(insurancePolicyInsureForPersonRequest.startTime) && StringKit.isInteger(insurancePolicyInsureForPersonRequest.endTime)) {
            if (Long.valueOf(insurancePolicyInsureForPersonRequest.endTime) <= Long.valueOf(insurancePolicyInsureForPersonRequest.startTime)) {
                return "错误";
            }
        } else {
            return "错误";
        }

        long time = System.currentTimeMillis();

        // TODO: 2018/3/23 记得校验部分参数
        InsurancePolicy.InsurancePolicyBaseBean insurancePolicyBaseBean = new InsurancePolicy.InsurancePolicyBaseBean();

        insurancePolicyBaseBean.userId = insurancePolicyInsureForPersonRequest.userId;
        insurancePolicyBaseBean.productId = insurancePolicyInsureForPersonRequest.productId;
        insurancePolicyBaseBean.privateCode = insurancePolicyBaseBean.createPrivateCode();

        insurancePolicyBaseBean.startTime = insurancePolicyInsureForPersonRequest.startTime;
        insurancePolicyBaseBean.endTime = insurancePolicyInsureForPersonRequest.endTime;
        insurancePolicyBaseBean.count = insurancePolicyInsureForPersonRequest.count;

        insurancePolicyBaseBean.createdTime = String.valueOf(time);
        insurancePolicyBaseBean.updatedTime = String.valueOf(time);

        InsurancePolicy.PersonInfo policyholder = new InsurancePolicy.PersonInfo();
        if (insurancePolicyInsureForPersonRequest.policyholder != null) {
            policyholder.name = insurancePolicyInsureForPersonRequest.policyholder.name;
            policyholder.cardType = insurancePolicyInsureForPersonRequest.policyholder.cardType;
            if (!policyholder.setCardCode(insurancePolicyInsureForPersonRequest.policyholder.cardCode)) {
                // 证件号码不符合规则
                return "错误";
            }
            policyholder.phone = insurancePolicyInsureForPersonRequest.policyholder.phone;

        } else {
            return "错误";
        }


        InsurancePolicy.PersonInfo insured = new InsurancePolicy.PersonInfo();

        if (insurancePolicyInsureForPersonRequest.insured != null) {
            insured.relationName = insurancePolicyInsureForPersonRequest.insured.relationName;
            insured.name = insurancePolicyInsureForPersonRequest.insured.name;
            insured.cardType = insurancePolicyInsureForPersonRequest.insured.cardType;
            if (!insured.setCardCode(insurancePolicyInsureForPersonRequest.insured.cardCode)) {
                // 证件号码不符合规则
                return "错误";
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
            insured.annualIncome = insurancePolicyInsureForPersonRequest.insured.annualIncome;
            insured.height = insurancePolicyInsureForPersonRequest.insured.height;
            insured.weight = insurancePolicyInsureForPersonRequest.insured.weight;

        } else {
            return "错误";
        }

        InsurancePolicy.PersonInfo beneficiary = new InsurancePolicy.PersonInfo();
        if (insurancePolicyInsureForPersonRequest.beneficiary != null) {
            beneficiary.relationName = insurancePolicyInsureForPersonRequest.beneficiary.relationName;
            beneficiary.name = insurancePolicyInsureForPersonRequest.beneficiary.name;
            beneficiary.cardType = insurancePolicyInsureForPersonRequest.beneficiary.cardType;
            if (!beneficiary.setCardCode(insurancePolicyInsureForPersonRequest.beneficiary.cardCode)) {
                // 证件号码不符合规则
                return "错误";
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
            return "错误";
        }


        // TODO: 2018/3/23 重新计算费用


        // TODO: 2018/3/23 成功后记得存保全记录
        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();

        insurancePreservationModel.cust_id = insurancePolicyInsureForPersonRequest.userId;
        insurancePreservationModel.private_code = insurancePolicyBaseBean.privateCode;
        insurancePreservationModel.event = String.valueOf(InsurancePreservationModel.EVENT_TYPE_INSURE);
        insurancePreservationModel.apply_time = String.valueOf(time);
        insurancePreservationModel.created_at = String.valueOf(time);

        insurancePreservationDao.addInsurancePreservation(insurancePreservationModel);

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

}
