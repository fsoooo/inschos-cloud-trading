package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicyBean;
import com.inschos.cloud.trading.access.http.controller.bean.PageBean;
import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.InsuranceRecordBean;
import com.inschos.cloud.trading.access.rpc.bean.ManagerUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.PolicyholderCountBean;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyPersonDao;
import com.inschos.cloud.trading.data.dao.CustWarrantyDao;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.JsonKit;
import com.inschos.common.assist.kit.L;
import com.inschos.common.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建日期：2018/4/24 on 15:31
 * 描述：
 * 作者：zhangyunhe
 */
@Service
public class CustWarrantyServiceImpl implements CustWarrantyService {

    @Autowired
    private CustWarrantyDao custWarrantyDao;

    @Autowired
    private CustWarrantyPersonDao custWarrantyPersonDao;
    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Override
    public String getPolicyholderCountByTimeOrAccountId(AccountUuidBean custWarrantyPolicyholderCountBean) {
        String result = "0";
        if (custWarrantyPolicyholderCountBean != null) {
            CustWarranty custWarranty = new CustWarranty();
            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.accountUuid)) {
                custWarranty.account_uuid = custWarrantyPolicyholderCountBean.accountUuid;
            } else {
                return result;
            }

            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.managerUuid)) {
                custWarranty.manager_uuid = custWarrantyPolicyholderCountBean.managerUuid;
            } else {
                return result;
            }

            custWarranty.start_time = custWarrantyPolicyholderCountBean.startTime;
            custWarranty.end_time = custWarrantyPolicyholderCountBean.endTime;
            long count = custWarrantyDao.findInsurancePolicyListCountByTimeAndAccountUuid(custWarranty);
            result = String.valueOf(count);
        }
        return result;
    }

    @Override
    public List<PolicyholderCountBean> getPolicyholderCountByTimeOrManagerUuid(ManagerUuidBean custWarrantyPolicyholderCountBean) {
        List<PolicyholderCountBean> result = new ArrayList<>();
        if (custWarrantyPolicyholderCountBean != null) {
            CustWarranty custWarranty = new CustWarranty();
            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.managerUuid)) {
                custWarranty.manager_uuid = custWarrantyPolicyholderCountBean.managerUuid;
            } else {
                return result;
            }

            if (custWarrantyPolicyholderCountBean.productIds == null) {
                return result;
            }

            StringBuilder stringBuilder = new StringBuilder();
            int size = custWarrantyPolicyholderCountBean.productIds.size();
            for (int i = 0; i < size; i++) {
                stringBuilder.append(custWarrantyPolicyholderCountBean.productIds.get(i));
                if (i < size - 1) {
                    stringBuilder.append(",");
                }
            }
            custWarranty.product_id_string = stringBuilder.toString();

            custWarranty.start_time = custWarrantyPolicyholderCountBean.startTime;
            custWarranty.end_time = custWarrantyPolicyholderCountBean.endTime;
            List<PolicyListCount> insurancePolicyListCountByTimeAndManagerUuidAndProductId = custWarrantyDao.findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(custWarranty);
            Map<String, PolicyListCount> map = new HashMap<>();

            for (PolicyListCount policyListCount : insurancePolicyListCountByTimeAndManagerUuidAndProductId) {
                map.put(policyListCount.product_id, policyListCount);
            }

            for (String productId : custWarrantyPolicyholderCountBean.productIds) {
                PolicyListCount policyListCount = map.get(productId);
                PolicyholderCountBean policyholderCountBean;
                if (policyListCount == null) {
                    policyholderCountBean = new PolicyholderCountBean(productId, 0);
                } else {
                    policyholderCountBean = new PolicyholderCountBean(policyListCount);
                }
                result.add(policyholderCountBean);
            }

        }
        return result;
    }


    @Override
    public int getPolicyCountByAgentStatus(CustWarranty search) {
        return custWarrantyDao.findCountByAUuidWarrantyStatus(search);
    }

    @Override
    public int getPolicyCountByCostStatus(CustWarranty search) {
        return custWarrantyDao.findCountByAUuidCostStatus(search);
    }

    @Override
    public InsuranceRecordBean getInsuranceRecord(InsuranceRecordBean insuranceRecord) {
        if (insuranceRecord == null) {
            return null;
        }

        if (StringKit.isEmpty(insuranceRecord.managerUuid) || StringKit.isEmpty(insuranceRecord.personType) || StringKit.isEmpty(insuranceRecord.cardType) || StringKit.isEmpty(insuranceRecord.cardCode)) {
            return insuranceRecord;
        }

        if (StringKit.isEmpty(insuranceRecord.pageSize)) {
            insuranceRecord.pageSize = "10";
        }

        L.log.debug(JsonKit.bean2Json(insuranceRecord));

        CustWarranty custWarranty = new CustWarranty();
        custWarranty.page = setPage(insuranceRecord.lastId, insuranceRecord.pageNum, insuranceRecord.pageSize);

        custWarranty.manager_uuid = insuranceRecord.managerUuid;
        custWarranty.person_type = insuranceRecord.personType;
        custWarranty.card_code = insuranceRecord.cardCode;
        custWarranty.card_type = insuranceRecord.cardType;

        List<CustWarranty> insuranceRecordListByManagerUuid = custWarrantyDao.findInsuranceRecordListByManagerUuid(custWarranty);
        long total = custWarrantyDao.findInsuranceRecordCountByManagerUuid(custWarranty);

        int listSize = 0;
        insuranceRecord.data = new ArrayList<>();

        if (insuranceRecordListByManagerUuid != null && !insuranceRecordListByManagerUuid.isEmpty()) {
            for (CustWarranty policyModel : insuranceRecordListByManagerUuid) {

                List<CustWarrantyPerson> insuranceParticipantInsuredByWarrantyUuid = custWarrantyPersonDao.findInsuranceParticipantInsuredByWarrantyUuid(policyModel.warranty_uuid);

                if (insuranceParticipantInsuredByWarrantyUuid != null && !insuranceParticipantInsuredByWarrantyUuid.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    int size = insuranceParticipantInsuredByWarrantyUuid.size();
                    for (int i = 0; i < size; i++) {
                        CustWarrantyPerson custWarrantyPerson = insuranceParticipantInsuredByWarrantyUuid.get(i);
                        sb.append(custWarrantyPerson.name);

                        if (StringKit.isEmpty(custWarrantyPerson.relation_name)) {
                            sb.append("(");
                            sb.append(custWarrantyPerson.relation_name);
                            sb.append(")");
                        }

                        if (i != size - 1) {
                            sb.append(",");
                        }
                    }
                    policyModel.insured_name = sb.toString();
                }

                CustWarrantyPerson policyHolderNameAndMobileByWarrantyUuid = custWarrantyPersonDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(policyModel.warranty_uuid);

                if("2".equals(policyModel.warranty_status) || "3".equals(policyModel.warranty_status) || "4".equals(policyModel.warranty_status) || "5".equals(policyModel.warranty_status)){
                    CustWarrantyCost costModel = new CustWarrantyCost();
                    costModel.warranty_uuid = policyModel.warranty_uuid;
                    CustWarrantyCost firstPhase = custWarrantyCostDao.findFirstPhase(costModel);
                    if(firstPhase!=null){
                        policyModel.premium = firstPhase.premium;
                        policyModel.pay_status = firstPhase.pay_status;
                        policyModel.pay_money = firstPhase.pay_money;
                    }
                }


                InsurancePolicyBean.GetInsurancePolicy insurancePolicy = new InsurancePolicyBean.GetInsurancePolicy(policyModel);

                insurancePolicy.policyholderText = policyHolderNameAndMobileByWarrantyUuid.name;
                insurancePolicy.insuredText = policyModel.insured_name;

                insuranceRecord.data.add(insurancePolicy);
            }
            listSize = insuranceRecordListByManagerUuid.size();
        }

        String lastId = "0";
        if (insuranceRecordListByManagerUuid != null && !insuranceRecordListByManagerUuid.isEmpty()) {
            lastId = insuranceRecordListByManagerUuid.get(insuranceRecordListByManagerUuid.size() - 1).id;
        }

        insuranceRecord.page = setPageBean(lastId, insuranceRecord.pageNum, insuranceRecord.pageSize, total, listSize);

        return insuranceRecord;
    }

    private Page setPage(String lastId, String num, String size) {
        Page page = new Page();

        if (StringKit.isInteger(size)) {
            if (StringKit.isInteger(lastId)) {
                page.lastId = Long.valueOf(lastId);
                page.offset = Integer.valueOf(size);
            } else if (StringKit.isInteger(num)) {
                int pageSize = Integer.valueOf(size);
                int pageStart = (Integer.valueOf(num) - 1) * pageSize;

                page.start = pageStart;
                page.offset = pageSize;
            }
        }
        return page;
    }

    protected PageBean setPageBean(String lastId, String page_num, String page_size, long total, int listSize) {
        PageBean pageBean = new PageBean();

        pageBean.lastId = lastId;
        pageBean.pageSize = StringKit.isInteger(page_size) ? page_size : "20";
        long l = total / Integer.valueOf(pageBean.pageSize);
        if (total % Integer.valueOf(pageBean.pageSize) != 0) {
            l += 1;
        }
        pageBean.pageNum = StringKit.isInteger(page_num) ? page_num : "1";
        pageBean.pageTotal = String.valueOf(l);
        pageBean.total = String.valueOf(total);
        pageBean.listSize = String.valueOf(listSize);

        return pageBean;
    }
}
