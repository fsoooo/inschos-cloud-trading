package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.TradeBean;
import com.inschos.cloud.trading.access.rpc.bean.PayCategoryBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductApiFromBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductUserBean;
import com.inschos.cloud.trading.access.rpc.client.InsureServiceClient;
import com.inschos.cloud.trading.access.rpc.client.ProductApiFromClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.access.rpc.client.ProductUserClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.WarrantyUuidWorker;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyDao;
import com.inschos.cloud.trading.model.CustWarranty;
import com.inschos.cloud.trading.model.CustWarrantyCost;
import com.inschos.cloud.trading.model.CustWarrantyPerson;
import com.inschos.common.assist.kit.JsonKit;
import com.inschos.common.assist.kit.MD5Kit;
import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;
import com.inschos.dock.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IceAnt on 2018/6/14.
 */
@Component
public class TradeAction extends BaseAction {

    @Autowired
    private ProductClient productClient;
    @Autowired
    private CustWarrantyDao custWarrantyDao;
    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private InsureServiceClient insureServiceClient;
    @Autowired
    private ProductUserClient productUserClient;

    @Autowired
    private ProductApiFromClient productApiFromClient;


    @Value("${dock.general.host}")
    private String dockGeneralHost;


    /**
     * 投保
     *
     * @param bean
     * @return
     */
    public String insure(ActionBean bean) {

        TradeBean.InsureRequest request = requst2Bean(bean.body, TradeBean.InsureRequest.class);
        TradeBean.InsureResponse response = new TradeBean.InsureResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }


        TradeBean.InsureRspData data = _addWarranty(bean, request, 1);


        if (data != null) {

            if (CustWarrantyCost.PAY_STATUS_WAIT.equals(data.status)) {
                response.data = data;
                return json(BaseResponse.CODE_SUCCESS, "待支付", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "投保失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "投保失败", response);
        }
    }

    /**
     * 支付
     *
     * @param bean
     * @return
     */
    public String pay(ActionBean bean) {

        TradeBean.InsurePayRequest request = requst2Bean(bean.body, TradeBean.InsurePayRequest.class);
        TradeBean.InsurePayResponse response = new TradeBean.InsurePayResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }


        String[] split = StringKit.split(request.payNo, "-");
        if (split != null && split.length > 1) {
            CustWarrantyCost search = new CustWarrantyCost();
            search.phase = split[1];
            search.warranty_uuid = split[0];
            CustWarrantyCost firstPhase = custWarrantyCostDao.findFirstPhase(search);
            if (firstPhase != null) {
                firstPhase.pay_status = CustWarrantyCost.PAY_STATUS_PROCESSING;
                firstPhase.updated_at = TimeKit.curTimeMillis2Str();
                boolean isOk = false;
                String msg = null;
                if (custWarrantyCostDao.updatePayStatusByWarrantyUuidPhase(firstPhase) > 0) {
                    PayBean payBean = new PayBean();
                    payBean.payNo = request.payNo;
                    payBean.payWay = toPayWay(request.payWay);
                    if (request.bankData != null) {
                        payBean.bankData = new BankBean();
                        payBean.bankData.userName = request.bankData.name;
                        payBean.bankData.phone = request.bankData.bankPhone;
                        payBean.bankData.idCard = request.bankData.certCode;
                        payBean.bankData.bankCode = request.bankData.bankCode;
                    }

                    CustWarranty policyModel = custWarrantyDao.findInsurancePolicyDetailByWarrantyUuid(firstPhase.warranty_uuid);

                    if (policyModel != null) {
                        ProductBean product = productClient.getProduct(Long.valueOf(policyModel.product_id));
                        if (product != null) {
                            payBean.key = bean.managerUuid;
                            payBean.secret = getSecret(bean.managerUuid);

                            RpcResponse<RspPayBean> rpcResponse = insureServiceClient.pay(payBean, getApiFromUuid(product.code));

                            if (rpcResponse == null || rpcResponse.code != RpcResponse.CODE_SUCCESS) {
                                firstPhase.pay_status = CustWarrantyCost.PAY_STATUS_FAILED;
                                firstPhase.updated_at = TimeKit.curTimeMillis2Str();
                                custWarrantyCostDao.updatePayStatusByWarrantyUuidPhase(firstPhase);
                                msg = null;
                                // TODO: 2018/6/25 修改请求信息
                            } else {
                                isOk = true;
                                msg = rpcResponse.message;
                            }
                        }
                    }

                }
                if (isOk) {
                    response.data = new TradeBean.InsureRspData();
                    response.data.status = CustWarrantyCost.PAY_STATUS_WAIT;
                    response.data.statusTxt = firstPhase.payStatusText(CustWarrantyCost.PAY_STATUS_WAIT);
                    response.data.warrantyUuid = firstPhase.warranty_uuid;
                    response.data.payNo = request.payNo;
                    return json(BaseResponse.CODE_SUCCESS, "缴费已发起", response);
                } else {
                    response.data = new TradeBean.InsureRspData();
                    response.data.status = CustWarrantyCost.PAY_STATUS_FAILED;
                    response.data.statusTxt = firstPhase.payStatusText(response.data.status);
                    response.data.warrantyUuid = firstPhase.warranty_uuid;
                    response.data.payNo = request.payNo;
                    return json(BaseResponse.CODE_FAILURE, msg != null ? msg : "缴费失败", response);
                }

            }

        } else {
            return json(BaseResponse.CODE_FAILURE, "暂无需缴费保单", response);
        }

        return json(BaseResponse.CODE_FAILURE, "暂无需缴费保单", response);
    }

    public String preInsure(ActionBean bean) {

        TradeBean.InsureRequest request = requst2Bean(bean.body, TradeBean.InsureRequest.class);
        TradeBean.PreInsureResponse response = new TradeBean.PreInsureResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);

        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }


        TradeBean.InsureRspData data = _addWarranty(bean, request, 2);

        if (data != null) {

            // TODO: 2018/6/20 预投保

            response.data = data;

            return json(BaseResponse.CODE_SUCCESS, "预投保成功", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, "预投保失败", response);
        }
    }

    public String quote(ActionBean bean) {

        TradeBean.QuoteRequest request = requst2Bean(bean.body, TradeBean.QuoteRequest.class);
        BaseResponse response = new BaseResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);

        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (StringKit.isInteger(request.productId)) {

            ProductBean product = productClient.getProduct(Long.valueOf(request.productId));
            if (product != null) {
                QuoteBean quoteBean = new QuoteBean();
                quoteBean.key = bean.managerUuid;
                quoteBean.secret = getSecret(bean.managerUuid);
                quoteBean.old_option = request.old_option;
                quoteBean.old_val = request.old_val;
                quoteBean.new_val = request.new_val;
                quoteBean.old_protect_item = request.old_protect_item;
                quoteBean.productCode = product.code;

                RpcResponse<String> rpcResponse = insureServiceClient.quote(quoteBean, getApiFromUuid(product.code));
                if (rpcResponse.code == RpcResponse.CODE_SUCCESS) {
                    response.data = JsonKit.json2Bean(rpcResponse.data, Object.class);
                    return json(BaseResponse.CODE_SUCCESS, "保费试算成功", response);
                } else {
                    return json(BaseResponse.CODE_FAILURE, "保费试算失败", response);
                }
            }


        }
        return json(BaseResponse.CODE_FAILURE, "保费试算失败", response);

    }


    /**
     * @param bean
     * @param request
     * @param method  1 投保 2预投保
     * @return
     */
    private TradeBean.InsureRspData _addWarranty(ActionBean bean, TradeBean.InsureRequest request, int method) {

        String accountUuid = bean.accountUuid;
        String managerUuid = bean.managerUuid;
        String agentId = null;
        String channelId = null;
        String planId = null;

        String curTime = TimeKit.curTimeMillis2Str();

        CustWarranty policyModel = new CustWarranty();
        String warrantyUuid = String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());
        policyModel.warranty_uuid = warrantyUuid;
        policyModel.account_uuid = accountUuid;
        policyModel.manager_uuid = managerUuid;
        policyModel.product_id = request.productId;
        policyModel.agent_id = agentId;
        policyModel.channel_id = channelId;
        policyModel.plan_id = planId;

        policyModel.start_time = request.startTime;
        policyModel.end_time = request.endTime;
        policyModel.count = request.count;
        policyModel.business_no = request.businessNo;
        policyModel.order_time = curTime;
        policyModel.warranty_status = CustWarranty.POLICY_STATUS_PENDING;

        policyModel.created_at = curTime;
        policyModel.updated_at = curTime;


        policyModel.insured_list = new ArrayList<>();


        //投保人
        policyModel.insured_list.add(request.policyholder.toParticipant(warrantyUuid, CustWarrantyPerson.TYPE_POLICYHOLDER, curTime, request.startTime, request.endTime));

        //受益人
        if (request.beneficiary != null) {
            policyModel.insured_list.add(request.beneficiary.toParticipant(warrantyUuid, CustWarrantyPerson.TYPE_BENEFICIARY, curTime, request.startTime, request.endTime));
        }

        //被保人
        if (request.recognizees != null) {
            for (TradeBean.InsurePersonData recognizee : request.recognizees) {
                policyModel.insured_list.add(recognizee.toParticipant(warrantyUuid, CustWarrantyPerson.TYPE_INSURED, curTime, request.startTime, request.endTime));
            }
        }

        long productId = Long.valueOf(request.productId);
        PayCategoryBean payCategory = null;
        //获取缴别信息
        if (StringKit.isInteger(request.payCategoryId)) {

            payCategory = productClient.getOnePayCategory(Long.valueOf(request.payCategoryId));

        } else {
            //获取产品所有缴别
            List<PayCategoryBean> payCategoryList = productClient.getListPayCategory(productId);

            if (payCategoryList != null) {
                for (PayCategoryBean categoryBean : payCategoryList) {
                    //取 缴期为一期的
                    if (categoryBean.times == 1) {
                        payCategory = categoryBean;
                        break;
                    }
                }
            }
        }
//        if (payCategory == null) {
//            payCategory = new PayCategoryBean();
//            payCategory.times = 1;
//            payCategory.name = "趸缴";
//        }
        if (payCategory != null) {
            policyModel.pay_category_id = String.valueOf(payCategory.id);


            InsureBean insureBean = tranBean(request);
            insureBean.warrantyUuid = warrantyUuid;
            insureBean.tradeTime = TimeKit.currentTimeMillis();
            insureBean.payCategory = payCategory.name;


            //获取保费
            String premium = null;

            ProductBean productBean = productClient.getPremium(insureBean);
            String productCode = null;

            if (productBean != null) {
                premium = productBean.basePrice;
                insureBean.premium = premium;
                insureBean.amount = productBean.amount;
                productCode = productBean.code;
            }

            List<CustWarrantyCost> costModels = new ArrayList<>();

            for (int i = 1; i <= payCategory.times; i++) {
                CustWarrantyCost costModel = new CustWarrantyCost();
                costModel.warranty_uuid = warrantyUuid;

                costModel.premium = premium;
                costModel.phase = String.valueOf(i);
                if (i == 1) {
                    costModel.pay_status = CustWarrantyCost.APPLY_UNDERWRITING_WAIT;
                    costModel.pay_time = request.startTime;
                } else {
                    costModel.pay_status = CustWarrantyCost.APPLY_UNDERWRITING_WAIT;
                    // TODO: 2018/6/14  pay_time
                    costModel.pay_time = request.startTime;
                }
                costModel.created_at = costModel.updated_at = curTime;
                costModels.add(costModel);
            }

            //地区拆分
            if (!StringKit.isEmpty(request.policyholder.area)) {
                String[] split = StringKit.split(request.policyholder.area, "-");
                if (split != null) {
                    if (split.length > 0) {
                        insureBean.province = split[0];
                        if (split.length > 1) {
                            insureBean.city = split[1];
                        }
                    }
                }
            }
            // TODO: 2018/6/25 insurePeriod  periodUnit
//                insureBean.insurePeriod;
//                insureBean.periodUnit;

            insureBean.key = bean.managerUuid;
            insureBean.secret = getSecret(bean.managerUuid);
            insureBean.productCode = productCode;

            TradeBean.InsureRspData data = new TradeBean.InsureRspData();
            if (method == 1) {
                RpcResponse<RspInsureBean> rpcResponse = insureServiceClient.insure(insureBean, getApiFromUuid(productCode));


                if (rpcResponse.data != null) {

                    policyModel.comb_product = rpcResponse.data.isCombProduct ? 1 : 0;
                    policyModel.comb_warranty_code = rpcResponse.data.combCardCode;
                    policyModel.pre_policy_no = rpcResponse.data.proposalNo;
                    policyModel.resp_msg = rpcResponse.message;

                }

                CustWarrantyCost costModel = costModels.get(0);
                costModel.warranty_uuid = warrantyUuid;
                costModel.phase = "1";


                if (rpcResponse.code == 200) {

                    costModel.pay_status = CustWarrantyCost.PAY_STATUS_WAIT;
                    data.status = costModel.pay_status;
                    data.statusTxt = costModel.payStatusText(data.status);
                    data.warrantyUuid = warrantyUuid;

                    if (rpcResponse.data != null) {
                        //缴费ID 为保单uuid 加缴费期组合而成
                        data.payNo = warrantyUuid + "-" + 1;
                        data.payType = rpcResponse.data.payType;
                        data.payUrl = rpcResponse.data.payUrl;
                    }
                } else {
                    costModel.pay_status = CustWarrantyCost.APPLY_UNDERWRITING_FAILURE;
                    data.status = costModel.pay_status;
                    if (StringKit.isEmpty(policyModel.pre_policy_no)) {
                        policyModel.warranty_status = CustWarranty.POLICY_STATUS_INVALID;
                    }
                }
            } else {
                RpcResponse<RspPreInsureBean> rpcResponse = insureServiceClient.preInsure(insureBean, getApiFromUuid(productCode));


            }

            custWarrantyDao.insure(policyModel, costModels);

            return data;

        }

        return null;
    }

    public String query(ActionBean bean) {

        TradeBean.QueryRequest request = requst2Bean(bean.body, TradeBean.QueryRequest.class);
        TradeBean.QueryResponse response = new TradeBean.QueryResponse();


        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        CustWarranty warranty = custWarrantyDao.findInsurancePolicyDetailByWarrantyUuid(request.warrantyUuid);

        TradeBean.PolicyData policyData = new TradeBean.PolicyData();

        if (warranty != null) {


            policyData.policyNo = warranty.warranty_code;
            policyData.proposalNo = warranty.pre_policy_no;
            policyData.warrantyUuid = request.warrantyUuid;
            policyData.remark = warranty.resp_msg;


            if (CustWarranty.APPLY_UNDERWRITING_PROCESSING.equals(warranty.warranty_status)) {
                CustWarrantyCost searchCost = new CustWarrantyCost();
                searchCost.warranty_uuid = request.warrantyUuid;
                searchCost.phase = "1";
                CustWarrantyCost warrantyCost = custWarrantyCostDao.findFirstPhase(searchCost);
                if (warrantyCost != null) {
                    policyData.status = warrantyCost.pay_status;

                }
                policyData.statusTxt = "待支付";
            } else {
                policyData.status = warranty.warranty_status;
                policyData.statusTxt = warranty.warrantyStatusText(warranty.warranty_status);

            }
        }
        response.data = policyData;
        return json(BaseResponse.CODE_SUCCESS, "获取成功", response);

    }


    private InsureBean tranBean(TradeBean.InsureRequest request) {
        InsureBean bean = new InsureBean();
        bean.startTime = request.startTime;
        bean.endTime = request.endTime;

        bean.count = request.count;
        bean.businessNo = request.businessNo;
        bean.businessParamsJson = request.businessParams;

        bean.policyholder = tranBean(request.policyholder);

        if (request.recognizees != null) {
            bean.recognizees = new ArrayList<>();
            for (TradeBean.InsurePersonData recognizee : request.recognizees) {
                bean.recognizees.add(tranBean(recognizee));
            }
        }
        bean.beneficiary = tranBean(request.beneficiary);
        return bean;

    }

    private PersonBean tranBean(TradeBean.InsurePersonData personData) {
        if (personData != null) {
            PersonBean bean = new PersonBean();
            bean.name = personData.name;
            if (StringKit.isInteger(personData.cardType)) {
                bean.cardType = Integer.valueOf(personData.cardType);
            }
            bean.cardCode = personData.cardCode;
            bean.phone = personData.phone;
            bean.birthday = personData.birthday;
            if (StringKit.isInteger(personData.relationName)) {
                bean.relation = Integer.valueOf(personData.relationName);
            }
            bean.area = personData.area;
            bean.address = personData.address;
            bean.age = personData.age;
            bean.email = personData.email;
            bean.annualIncome = personData.annualIncome;
            bean.weight = personData.weight;
            bean.height = personData.height;
            if (StringKit.isInteger(personData.sex)) {
                bean.sex = Integer.valueOf(personData.sex);
            }
            bean.occupation = personData.occupation;
            bean.nationality = personData.nationality;

            return bean;
        } else {
            return null;
        }
    }

    private PayBean.PayWay toPayWay(String way) {
        PayBean.PayWay payWay;
        switch (way) {
            case "1":
                payWay = PayBean.PayWay.UNIONPAY;
                break;
            case "2":
                payWay = PayBean.PayWay.ALIPAY;
                break;
            case "3":
                payWay = PayBean.PayWay.WEIXIN;
                break;
            case "4":
                payWay = PayBean.PayWay.CASH;
                break;
            case "5":
                payWay = PayBean.PayWay.WITHHOLD;
                break;
            case "6":
                payWay = PayBean.PayWay.THIRD_PARTY_PAY;
                break;
            default:
                payWay = PayBean.PayWay.OTHER;
                break;
        }
        return payWay;
    }


    private String getSecret(String key) {
        key = "14222842946260992";
        ProductUserBean userBean = productUserClient.getProductUser(key);
        if (userBean != null) {
            return userBean.sign_key;
        }else{
            userBean = new ProductUserBean();
            userBean.account_id = key;
            userBean.call_back_url = dockGeneralHost;
            userBean.name = "cloud调用";
            userBean.email = "rd@inschos.com";
            userBean.password = "";
            userBean.sign_key = MD5Kit.MD5Digest(key+TimeKit.curTimeMillis2Str());
            userBean.sell_status = 1;
            int i = productUserClient.add(userBean);
            if(i>0){
                return userBean.sign_key;
            }
        }
        return null;
    }

    private String getApiFromUuid(String productCode){

        ProductApiFromBean apiFrom = productApiFromClient.getApiFrom(productCode);
        if(apiFrom!=null){
            return apiFrom.apiUuid;
        }

        return null;
    }

}
