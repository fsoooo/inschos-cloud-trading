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

    public String insure(InsurancePolicy.InsurancePolicyInsureRequest insurancePolicyInsureRequest) {

        // 校验有效性后，重新计算保费，用重新计算的保费写数据库，并生成订单。

        if (StringKit.isInteger(insurancePolicyInsureRequest.startTime) && StringKit.isInteger(insurancePolicyInsureRequest.endTime)) {
            if (Long.valueOf(insurancePolicyInsureRequest.endTime) <= Long.valueOf(insurancePolicyInsureRequest.startTime)) {
                return "错误";
            }
        } else {
            return "错误";
        }

        long time = System.currentTimeMillis();

        // TODO: 2018/3/23 记得校验部分参数
        InsurancePolicy.InsurancePolicyBaseBean insurancePolicyBaseBean = new InsurancePolicy.InsurancePolicyBaseBean();

        insurancePolicyBaseBean.userId = insurancePolicyInsureRequest.userId;
        insurancePolicyBaseBean.productId = insurancePolicyInsureRequest.productId;
        insurancePolicyBaseBean.privateCode = insurancePolicyBaseBean.createPrivateCode();

        insurancePolicyBaseBean.startTime = insurancePolicyInsureRequest.startTime;
        insurancePolicyBaseBean.endTime = insurancePolicyInsureRequest.endTime;
        insurancePolicyBaseBean.count = insurancePolicyInsureRequest.count;

        insurancePolicyBaseBean.createdTime = String.valueOf(time);
        insurancePolicyBaseBean.updatedTime = String.valueOf(time);

        InsurancePolicy.PersonInfo policyholder = new InsurancePolicy.PersonInfo();
        if (insurancePolicyInsureRequest.policyholder != null) {
            policyholder.name = insurancePolicyInsureRequest.policyholder.name;
            policyholder.cardType = insurancePolicyInsureRequest.policyholder.cardType;
            if (!policyholder.setCardCode(insurancePolicyInsureRequest.policyholder.cardCode)) {
                // 证件号码不符合规则
                return "错误";
            }
            policyholder.phone = insurancePolicyInsureRequest.policyholder.phone;

        } else {
            return "错误";
        }


        InsurancePolicy.PersonInfo insured = new InsurancePolicy.PersonInfo();

        if (insurancePolicyInsureRequest.insured != null) {
            insured.relationName = insurancePolicyInsureRequest.insured.relationName;
            insured.name = insurancePolicyInsureRequest.insured.name;
            insured.cardType = insurancePolicyInsureRequest.insured.cardType;
            if (!insured.setCardCode(insurancePolicyInsureRequest.insured.cardCode)) {
                // 证件号码不符合规则
                return "错误";
            }
            insured.phone = insurancePolicyInsureRequest.insured.phone;
            insured.birthday = insurancePolicyInsureRequest.insured.birthday;
            insured.sex = insurancePolicyInsureRequest.insured.sex;
            insured.phone = insurancePolicyInsureRequest.insured.phone;
            insured.occupation = insurancePolicyInsureRequest.insured.occupation;
            insured.email = insurancePolicyInsureRequest.insured.email;
            insured.area = insurancePolicyInsureRequest.insured.area;
            insured.address = insurancePolicyInsureRequest.insured.address;
            insured.nationality = insurancePolicyInsureRequest.insured.nationality;
            insured.annualIncome = insurancePolicyInsureRequest.insured.annualIncome;
            insured.height = insurancePolicyInsureRequest.insured.height;
            insured.weight = insurancePolicyInsureRequest.insured.weight;

        } else {
            return "错误";
        }

        InsurancePolicy.PersonInfo beneficiary = new InsurancePolicy.PersonInfo();
        if (insurancePolicyInsureRequest.beneficiary != null) {
            beneficiary.relationName = insurancePolicyInsureRequest.beneficiary.relationName;
            beneficiary.name = insurancePolicyInsureRequest.beneficiary.name;
            beneficiary.cardType = insurancePolicyInsureRequest.beneficiary.cardType;
            if (!beneficiary.setCardCode(insurancePolicyInsureRequest.beneficiary.cardCode)) {
                // 证件号码不符合规则
                return "错误";
            }
            beneficiary.phone = insurancePolicyInsureRequest.beneficiary.phone;
            beneficiary.birthday = insurancePolicyInsureRequest.beneficiary.birthday;
            beneficiary.sex = insurancePolicyInsureRequest.beneficiary.sex;
            beneficiary.phone = insurancePolicyInsureRequest.beneficiary.phone;
            beneficiary.email = insurancePolicyInsureRequest.beneficiary.email;
            beneficiary.area = insurancePolicyInsureRequest.beneficiary.area;
            beneficiary.address = insurancePolicyInsureRequest.beneficiary.address;
            beneficiary.nationality = insurancePolicyInsureRequest.beneficiary.nationality;

        } else {
            return "错误";
        }


        // TODO: 2018/3/23 重新计算费用


        // TODO: 2018/3/23 成功后记得存保全记录
        InsurancePreservationModel insurancePreservationModel = new InsurancePreservationModel();

        insurancePreservationModel.cust_id = insurancePolicyInsureRequest.userId;
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
