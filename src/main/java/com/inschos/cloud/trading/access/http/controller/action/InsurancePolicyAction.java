package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建日期：2018/3/22 on 11:06
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicyAction extends BaseAction {

    public String findInsurancePolicyListByUserIdAndStatus(String userId, int status) {

        // 状态0为全部查



        return "";
    }

    public String findInsurancePolicyListByOtherInfo(InsurancePolicy.InsurancePolicyListByOtherInfoRequest otherInfo) {

        // 状态0为全部查



        return "";
    }

    public String findInsurancePolicyDetailByPrivateCode(int type, String privateCode) {

        // TODO: 2018/3/22 判断用户类型

        // 企业

        // 个人


        return "";
    }

}
