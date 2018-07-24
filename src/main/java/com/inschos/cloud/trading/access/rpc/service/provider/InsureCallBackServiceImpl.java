package com.inschos.cloud.trading.access.rpc.service.provider;


import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBrokerageBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBrokerageInfoBean;
import com.inschos.cloud.trading.access.rpc.client.InsureServiceClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyPersonDao;
import com.inschos.cloud.trading.model.CustWarranty;
import com.inschos.cloud.trading.model.CustWarrantyBrokerage;
import com.inschos.cloud.trading.model.CustWarrantyCost;
import com.inschos.cloud.trading.model.CustWarrantyPerson;
import com.inschos.common.assist.kit.JsonKit;
import com.inschos.common.assist.kit.L;
import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;
import com.inschos.dock.api.CallBackService;
import com.inschos.dock.bean.IssueBean;
import com.inschos.dock.bean.RpcResponse;
import com.inschos.dock.bean.RspIssueBean;
import com.inschos.dock.bean.RspPayBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IceAnt on 2018/6/25.
 */
@Service
public class InsureCallBackServiceImpl implements CallBackService {

    @Autowired
    private CustWarrantyDao custWarrantyDao;
    @Autowired
    private CustWarrantyPersonDao custWarrantyPersonDao;
    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private InsureServiceClient insureServiceClient;
    @Autowired
    private ProductClient productClient;



    @Override
    public void outPolicy(RpcResponse<RspPayBean> payBean) {

        if (payBean != null && payBean.data != null) {

            String proposalNo = payBean.data.proposalNo;

            CustWarranty search = new CustWarranty();
            search.pre_policy_no = proposalNo;
            CustWarranty policy = custWarrantyDao.findByProposalNo(search);

            if(policy==null){
                return;
            }

            //缴费信息
            CustWarrantyCost searchCost = new CustWarrantyCost();
            searchCost.manager_uuid = policy.manager_uuid;
            searchCost.phase = "1";
            CustWarrantyCost cost = custWarrantyCostDao.findFirstPhase(searchCost);

            int result = 0;

            if (payBean.code == RpcResponse.CODE_SUCCESS) {


                if (payBean.data.needQuery) {

                    //出单

                    List<CustWarrantyPerson> personList = custWarrantyPersonDao.findInsuranceParticipantInsuredByWarrantyUuid(policy.warranty_uuid);
                    String productCode = null;
                    if (StringKit.isInteger(policy.product_id)) {
                        ProductBean product = productClient.getProduct(Long.valueOf(policy.product_id));
                        if (product != null) {
                            productCode = product.code;
                        }
                    }
                    boolean isIssue = false;
                    if (personList != null && !personList.isEmpty()) {
                        for (CustWarrantyPerson warrantyPerson : personList) {
                            if (!StringKit.isEmpty(warrantyPerson.out_order_no)) {
                                IssueBean bean = new IssueBean();
                                bean.proposalNo = policy.pre_policy_no;
                                bean.productCode = productCode;
                                bean.orderNo = warrantyPerson.out_order_no;
                                RpcResponse<RspIssueBean> rpcResponse = insureServiceClient.issue(bean, productCode);
                                isIssue = true;

                                if (rpcResponse != null && rpcResponse.code == RpcResponse.CODE_SUCCESS && rpcResponse.data != null) {
                                    if (StringKit.isEmpty(policy.warranty_code)) {

                                        policy.warranty_code = rpcResponse.data.policyNo;
                                    } else {
                                        policy.warranty_code = policy.warranty_code + "," + rpcResponse.data.policyNo;
                                    }

                                    if(StringKit.isEmpty(rpcResponse.data.orderNo)){
                                        break;
                                    }
                                }

                            }

                        }
                    }
                    long millis = TimeKit.currentTimeMillis();
                    if (!isIssue) {
                        //出单
                        IssueBean bean = new IssueBean();
                        bean.proposalNo = policy.pre_policy_no;
                        bean.productCode = productCode;
                        RpcResponse<RspIssueBean> rpcResponse = insureServiceClient.issue(bean, productCode);
                        if (rpcResponse != null && rpcResponse.code == RpcResponse.CODE_SUCCESS && rpcResponse.data != null) {
                            policy.warranty_code = rpcResponse.data.policyNo;
                            if (!StringKit.isInteger(rpcResponse.data.startTime)) {
                                policy.start_time = rpcResponse.data.startTime;
                            }
                            if (!StringKit.isInteger(rpcResponse.data.endTime)) {
                                policy.end_time = rpcResponse.data.endTime;
                            }

                            //保单状态
                            if (millis > Long.valueOf(policy.end_time)) {
                                policy.warranty_status = CustWarranty.POLICY_STATUS_EXPIRED;
                            } else if (millis < Long.valueOf(policy.start_time)) {
                                policy.warranty_status = CustWarranty.POLICY_STATUS_WAITING;
                            } else {
                                policy.warranty_status = CustWarranty.POLICY_STATUS_EFFECTIVE;
                            }

                        } else {
                            policy.warranty_status = CustWarranty.POLICY_STATUS_WAITING;
                        }
                    } else {
                        //保单状态
                        if (millis > Long.valueOf(policy.end_time)) {
                            policy.warranty_status = CustWarranty.POLICY_STATUS_EXPIRED;
                        } else if (millis < Long.valueOf(policy.start_time)) {
                            policy.warranty_status = CustWarranty.POLICY_STATUS_WAITING;
                        } else {
                            policy.warranty_status = CustWarranty.POLICY_STATUS_EFFECTIVE;
                        }
                    }


                } else {
                    policy.warranty_code = payBean.data.policyNo;

                    long millis = TimeKit.currentTimeMillis();

                    if (millis > Long.valueOf(policy.end_time)) {
                        policy.warranty_status = CustWarranty.POLICY_STATUS_EXPIRED;
                    } else if (millis < Long.valueOf(policy.start_time)) {
                        policy.warranty_status = CustWarranty.POLICY_STATUS_WAITING;
                    } else {
                        policy.warranty_status = CustWarranty.POLICY_STATUS_EFFECTIVE;
                    }
                }


                String millis2Str = TimeKit.curTimeMillis2Str();

                policy.updated_at = millis2Str;


                CustWarrantyBrokerage custWarrantyBrokerage = null;

                if(cost!=null){
                    cost.updated_at = millis2Str;
                    if(!StringKit.isEmpty(payBean.data.payMoney)){

                        cost.pay_money = payBean.data.payMoney;
                    }
                    if(!StringKit.isEmpty(payBean.data.premium)){
                        cost.premium = payBean.data.premium;
                    }
                    cost.actual_pay_time = StringKit.isInteger(payBean.data.tradeTime) ? payBean.data.tradeTime : millis2Str;
                    cost.pay_status = CustWarrantyCost.PAY_STATUS_SUCCESS;


                    ProductBrokerageBean searchBrokerageSeting = new ProductBrokerageBean();
                    searchBrokerageSeting.agentId = StringKit.isInteger(policy.agent_id)?Long.valueOf(policy.agent_id):0;
                    searchBrokerageSeting.channelId = StringKit.isInteger(policy.channel_id)?Long.valueOf(policy.channel_id):0;
                    searchBrokerageSeting.productId = Long.valueOf(policy.product_id);
                    searchBrokerageSeting.managerUuid = policy.manager_uuid;
                    searchBrokerageSeting.payCategoryId = Long.valueOf(policy.pay_category_id);
                    searchBrokerageSeting.payTimes = 1;
                    ProductBrokerageInfoBean brokerage = productClient.getBrokerage(searchBrokerageSeting);

                    BigDecimal warrantyBrokerage = new BigDecimal("0.00");
                    BigDecimal insBrokerage = new BigDecimal("0.00");
                    BigDecimal managerBrokerage = new BigDecimal("0.00");
                    BigDecimal channelBrokerage = new BigDecimal("0.00");
                    BigDecimal agentBrokerage = new BigDecimal("0.00");

                    BigDecimal warrantyRate = new BigDecimal("0.00");
                    BigDecimal insRate = new BigDecimal("0.00");
                    BigDecimal managerRate = new BigDecimal("0.00");
                    BigDecimal channelRate = new BigDecimal("0.00");
                    BigDecimal agentRate = new BigDecimal("0.00");


                    BigDecimal premium = new BigDecimal(cost.premium);

                    if (brokerage != null) {

                        warrantyRate = new BigDecimal(brokerage.basicBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);
                        insRate = new BigDecimal(brokerage.insBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);
                        managerRate = new BigDecimal(brokerage.platformBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);
                        channelRate = new BigDecimal(brokerage.channelBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);
                        agentRate = new BigDecimal(brokerage.agentBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);

                        warrantyBrokerage = warrantyRate.multiply(premium);
                        insBrokerage = insRate.multiply(premium);
                        managerBrokerage = managerRate.multiply(premium);
                        channelBrokerage = channelRate.multiply(premium);
                        agentBrokerage = agentRate.multiply(premium);
                    }

                    custWarrantyBrokerage = new CustWarrantyBrokerage();

                    custWarrantyBrokerage.warranty_uuid = policy.warranty_uuid;
                    custWarrantyBrokerage.agent_id = policy.agent_id;
                    custWarrantyBrokerage.channel_id = policy.channel_id;
                    custWarrantyBrokerage.manager_uuid = policy.manager_uuid;
                    custWarrantyBrokerage.created_at = custWarrantyBrokerage.updated_at = millis2Str;
                    custWarrantyBrokerage.cost_id = cost.id;
                    custWarrantyBrokerage.setBrokerage(warrantyBrokerage, insBrokerage, managerBrokerage, channelBrokerage, agentBrokerage);
                    custWarrantyBrokerage.setBrokerageRate(warrantyRate, insRate, managerRate, channelRate, agentRate);

                }

                result = custWarrantyDao.updatePayResult(policy,cost,custWarrantyBrokerage);

            }else{
                String millis2Str = TimeKit.curTimeMillis2Str();
                cost.pay_status = CustWarrantyCost.PAY_STATUS_FAILED;

                cost.updated_at = millis2Str;


                policy.resp_code = payBean.esbErrCode;
                policy.resp_msg = payBean.message;
                policy.updated_at = millis2Str;

                result = custWarrantyDao.updatePayResult(policy,cost,null);
            }
            if(result==0){
                L.log.error("outPolicy failed ,the pay bean :{}", JsonKit.bean2Json(payBean));
            }
        }

    }
}
