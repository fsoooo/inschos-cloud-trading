package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseRequest;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.StatisticBean;
import com.inschos.cloud.trading.access.http.controller.bean.StatisticBean.InsureStsItem;
import com.inschos.cloud.trading.access.http.controller.bean.StatisticBean.InsureStsTotalForAgentSelfResponse;
import com.inschos.cloud.trading.access.http.controller.bean.StatisticBean.InsureTotalData;
import com.inschos.cloud.trading.access.http.controller.bean.StatisticBean.InsureTotalStatistic;
import com.inschos.cloud.trading.access.rpc.bean.AgentBean;
import com.inschos.cloud.trading.access.rpc.client.AgentClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.data.dao.CustWarrantyBrokerageDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyDao;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by IceAnt on 2018/5/16.
 */
@Component
public class StatisticAction extends BaseAction {
    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;
    @Autowired
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;
    @Autowired
    private CustWarrantyDao custWarrantyDao;
    @Autowired
    private AgentClient agentClient;


    public String insureTotalForAgentSelf(ActionBean bean){
        BaseRequest request = requst2Bean(bean.body, BaseRequest.class);
        InsureStsTotalForAgentSelfResponse response = new InsureStsTotalForAgentSelfResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if(bean.userType!=4){
            return json(BaseResponse.CODE_FAILURE, "非代理人登录", response);
        }


        AgentBean agentBean = agentClient.getAgentInfoByPersonIdManagerUuid(bean.managerUuid, Long.valueOf(bean.userId));

        if(agentBean!=null){
            String agentId = String.valueOf(agentBean.id);
            response.data = new InsureTotalData();
            response.data.premium= _agentTotalPremium(bean.managerUuid,agentId);
            response.data.brokerage= _agentTotalBrokerage(bean.managerUuid,agentId);
            response.data.warranty = _agentTotalWarrantyCount(bean.managerUuid,agentId);
            return json(BaseResponse.CODE_SUCCESS, "获取保单统计成功", response);
        }else{
            return json(BaseResponse.CODE_FAILURE, "获取失败", response);
        }


    }
    public String insureListForAgentSelf(ActionBean bean){

        StatisticBean.InsureStsListForAgentSelfRequest request = requst2Bean(bean.body, StatisticBean.InsureStsListForAgentSelfRequest.class);
        StatisticBean.InsureStsListForAgentSelfResponse response = new StatisticBean.InsureStsListForAgentSelfResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if(bean.userType!=4){
            return json(BaseResponse.CODE_FAILURE, "非代理人登录", response);
        }
        String agentId = null;
        AgentBean agentBean = agentClient.getAgentInfoByPersonIdManagerUuid(bean.managerUuid, Long.valueOf(bean.userId));
        if(agentBean!=null) {
            agentId = String.valueOf(agentBean.id);
        }else{
            return json(BaseResponse.CODE_FAILURE, "获取失败", response);
        }


        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
        CustWarrantyBrokerage custWarrantyBrokerage = new CustWarrantyBrokerage();


        String startTime = request.startTime;
        String endTime = request.endTime;

        // 时间范围类型，2-按月，3-按年

        custWarrantyCost.start_time = startTime;
        custWarrantyCost.end_time = endTime;
        custWarrantyCost.time_range_type = request.timeRangeType;
        custWarrantyCost.manager_uuid = bean.managerUuid;
        custWarrantyCost.agent_id = agentId;

        custWarrantyBrokerage.start_time = startTime;
        custWarrantyBrokerage.end_time = endTime;
        custWarrantyBrokerage.time_range_type = request.timeRangeType;
        custWarrantyBrokerage.manager_uuid = bean.managerUuid;
        custWarrantyBrokerage.agent_id = agentId;

        List<PremiumStatistic> custWarrantyCostStatistic = custWarrantyCostDao.findCustWarrantyCostStatistic(custWarrantyCost);
        List<BrokerageStatistic> custWarrantyBrokerageStatistic = custWarrantyBrokerageDao.findStatisticByAgent(custWarrantyBrokerage);

        LinkedHashMap<String, InsureStsItem> map = new LinkedHashMap<>();

        response.data = new ArrayList<>();


        BigDecimal premium = new BigDecimal("0.00");
        int count = 0;
        if (custWarrantyCostStatistic != null && !custWarrantyCostStatistic.isEmpty()) {
            for (PremiumStatistic premiumStatistic : custWarrantyCostStatistic) {
                InsureStsItem item = new InsureStsItem(premiumStatistic.time_text,request.timeRangeType);
                item.setPremiumStatisticModel(premiumStatistic);
                map.put(premiumStatistic.time_text, item);
                premium = premium.add(new BigDecimal(premiumStatistic.premium));

                if (StringKit.isInteger(premiumStatistic.insurance_policy_count)) {
                    count += Integer.valueOf(premiumStatistic.insurance_policy_count);
                }
            }
        }

        BigDecimal brokerage = new BigDecimal("0.00");
        if (custWarrantyBrokerageStatistic != null && !custWarrantyBrokerageStatistic.isEmpty()) {
            for (BrokerageStatistic brokerageStatistic : custWarrantyBrokerageStatistic) {
                InsureStsItem item = map.getOrDefault(brokerageStatistic.time_text,null);
                if (item == null) {
                    item = new InsureStsItem(brokerageStatistic.time_text,request.timeRangeType);
                    map.put(brokerageStatistic.time_text, item);
                }
                item.setBrokerageStatisticModel(brokerageStatistic);
                brokerage = brokerage.add(new BigDecimal(brokerageStatistic.brokerage));
            }
        }
        response.data = dealPercentageByList(map, premium, brokerage);

        return json(BaseResponse.CODE_SUCCESS, "获取统计信息成功", response);
    }




    private List<InsureStsItem> dealPercentageByList(LinkedHashMap<String, InsureStsItem> map, BigDecimal premium, BigDecimal brokerage) {
        List<InsureStsItem> result = new ArrayList<>();
        Set<String> strings = map.keySet();
        if (!strings.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            for (String string : strings) {
                InsureStsItem item = map.get(string);

                BigDecimal itemPremium;
                if (StringKit.isEmpty(item.premium) && !StringKit.isNumeric(item.premium)) {
                    itemPremium = new BigDecimal("0.00");
                    item.premium = "0.00";
                    item.premiumText = "¥0.00";
                } else {
                    itemPremium = new BigDecimal(item.premium);
                }

                BigDecimal itemBrokerage;
                if (StringKit.isEmpty(item.brokerage) && !StringKit.isNumeric(item.brokerage)) {
                    itemBrokerage = new BigDecimal("0.00");
                    item.brokerage = "0.00";
                    item.brokerageText = "¥0.00";
                } else {
                    itemBrokerage = new BigDecimal(item.brokerage);
                }


                if (itemPremium.compareTo(BigDecimal.ZERO) != 0) {

                } else {
                    item.premium = "0.00";
                    item.premiumText = "¥0.00";

                }
                if(StringKit.isEmpty(item.insurancePolicyCount)){
                    item.insurancePolicyCount = "0";
                }
                item.premium = decimalFormat.format(itemPremium.doubleValue());
                item.premiumText = "¥" + item.premium;
                item.brokerage = decimalFormat.format(itemBrokerage.doubleValue());
                item.brokerageText = "¥" + item.brokerage;


                result.add(item);
            }
        }

        return result;
    }


    private InsureTotalStatistic _agentTotalBrokerage(String managerUuid,String agentId){

        CustWarrantyBrokerage custWarrantyBrokerage = new CustWarrantyBrokerage();



        custWarrantyBrokerage.manager_uuid = managerUuid;
        custWarrantyBrokerage.agent_id = agentId;


        //noinspection MagicConstant
        custWarrantyBrokerage.start_time = String.valueOf(TimeKit.getMonthStartTime());
        custWarrantyBrokerage.end_time = String.valueOf(TimeKit.getMonthEndTime());

        // 当月的所有付款的
        String monthAmount = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByManagerUuid(custWarrantyBrokerage);

        custWarrantyBrokerage.start_time = "0";
        custWarrantyBrokerage.end_time = "0";

        String totalAmount = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByManagerUuid(custWarrantyBrokerage);

        InsureTotalStatistic statistic = new InsureTotalStatistic();
        statistic.monthAmount = monthAmount;
        statistic.totalAmount = totalAmount;
        return statistic;
    }

    private InsureTotalStatistic _agentTotalPremium(String managerUuid,String agentId){


        CustWarrantyCost custWarrantyCost = new CustWarrantyCost();

        custWarrantyCost.manager_uuid = managerUuid;
        custWarrantyCost.agent_id = agentId;

        //noinspection MagicConstant
        custWarrantyCost.start_time = String.valueOf(TimeKit.getMonthStartTime());

        custWarrantyCost.end_time = String.valueOf(TimeKit.getMonthEndTime());

        // 当月的所有付款的
        String monthAmount = custWarrantyCostDao.findCustWarrantyCostTotalByManagerUuid(custWarrantyCost);

        custWarrantyCost.start_time = "0";
        custWarrantyCost.end_time = "0";

        String totalAmount = custWarrantyCostDao.findCustWarrantyCostTotalByManagerUuid(custWarrantyCost);

        InsureTotalStatistic statistic = new InsureTotalStatistic();
        statistic.monthAmount = monthAmount;
        statistic.totalAmount = totalAmount;

        return statistic;
    }

    private InsureTotalStatistic _agentTotalWarrantyCount(String managerUuid,String agentId){


        CustWarranty search = new CustWarranty();

        search.manager_uuid = managerUuid;
        search.agent_id = agentId;

        //noinspection MagicConstant
        search.start_time = String.valueOf(TimeKit.getMonthStartTime());

        search.end_time = String.valueOf(TimeKit.getMonthEndTime());

        // 当月的所有付款的
        int monthAmount = custWarrantyDao.findEffectiveInsurancePolicyCountByAgentAndTime(search);

        search.start_time = "0";
        search.end_time = "0";

        int totalAmount = custWarrantyDao.findEffectiveInsurancePolicyCountByAgentAndTime(search);

        InsureTotalStatistic statistic = new InsureTotalStatistic();
        statistic.monthAmount = String.valueOf(monthAmount);
        statistic.totalAmount = String.valueOf(totalAmount);

        return statistic;
    }

}
