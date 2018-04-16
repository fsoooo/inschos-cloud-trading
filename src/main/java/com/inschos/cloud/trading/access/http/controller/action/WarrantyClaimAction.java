package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseRequest;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.data.dao.CustWarrantyClaimsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/4/12.
 */
@Component
public class WarrantyClaimAction extends BaseAction{

    @Autowired
    private CustWarrantyClaimsDao custWarrantyClaimsDao;

    /**
     * 检查是否允许理赔
     * @param bean
     * @return
     */
    public String  check(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body,BaseRequest.class);
        BaseResponse response = new BaseResponse();

        return json(BaseResponse.CODE_FAILURE,"业务完善中",response);
    }

    /**
     * 被保人列表
     * @param bean
     * @return
     */
    public String insuredPersonList(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body,BaseRequest.class);
        BaseResponse response = new BaseResponse();

        return json(BaseResponse.CODE_FAILURE,"业务完善中",response);
    }

    /**
     * 出险类型
     * @param bean
     * @return
     */
    public String claimType(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body,BaseRequest.class);
        BaseResponse response = new BaseResponse();

        return json(BaseResponse.CODE_FAILURE,"业务完善中",response);
    }

    /**
     * 出险信息
     * @param bean
     * @return
     */
    public String claimInfo(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body,BaseRequest.class);
        BaseResponse response = new BaseResponse();

        return json(BaseResponse.CODE_FAILURE,"业务完善中",response);
    }

    /**
     * 申请理赔
     * @param bean
     * @return
     */
    public String apply(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body,BaseRequest.class);
        BaseResponse response = new BaseResponse();

        return json(BaseResponse.CODE_FAILURE,"业务完善中",response);
    }

    /**
     * 递交材料
     * @param bean
     * @return
     */
    public String presentMaterial(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body,BaseRequest.class);
        BaseResponse response = new BaseResponse();

        return json(BaseResponse.CODE_FAILURE,"业务完善中",response);
    }



}
