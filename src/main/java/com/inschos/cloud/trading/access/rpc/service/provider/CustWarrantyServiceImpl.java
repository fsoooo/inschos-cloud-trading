package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.http.controller.bean.PageBean;
import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.InsuranceRecordBean;
import com.inschos.cloud.trading.access.rpc.bean.ManagerUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.PolicyholderCountBean;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.data.dao.InsuranceParticipantDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.Page;
import com.inschos.cloud.trading.model.PolicyListCountModel;
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
    private InsurancePolicyDao insurancePolicyDao;

    @Autowired
    private InsuranceParticipantDao insuranceParticipantDao;

    @Override
    public String getPolicyholderCountByTimeOrAccountId(AccountUuidBean custWarrantyPolicyholderCountBean) {
        String result = "0";
        if (custWarrantyPolicyholderCountBean != null) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.accountUuid)) {
                insurancePolicyModel.account_uuid = custWarrantyPolicyholderCountBean.accountUuid;
            } else {
                return result;
            }

            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.managerUuid)) {
                insurancePolicyModel.manager_uuid = custWarrantyPolicyholderCountBean.managerUuid;
            } else {
                return result;
            }

            insurancePolicyModel.start_time = custWarrantyPolicyholderCountBean.startTime;
            insurancePolicyModel.end_time = custWarrantyPolicyholderCountBean.endTime;
            long count = insurancePolicyDao.findInsurancePolicyListCountByTimeAndAccountUuid(insurancePolicyModel);
            result = String.valueOf(count);
        }
        return result;
    }

    @Override
    public List<PolicyholderCountBean> getPolicyholderCountByTimeOrManagerUuid(ManagerUuidBean custWarrantyPolicyholderCountBean) {
        List<PolicyholderCountBean> result = new ArrayList<>();
        if (custWarrantyPolicyholderCountBean != null) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.managerUuid)) {
                insurancePolicyModel.manager_uuid = custWarrantyPolicyholderCountBean.managerUuid;
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
            insurancePolicyModel.product_id_string = stringBuilder.toString();

            insurancePolicyModel.start_time = custWarrantyPolicyholderCountBean.startTime;
            insurancePolicyModel.end_time = custWarrantyPolicyholderCountBean.endTime;
            List<PolicyListCountModel> insurancePolicyListCountByTimeAndManagerUuidAndProductId = insurancePolicyDao.findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(insurancePolicyModel);
            Map<String, PolicyListCountModel> map = new HashMap<>();

            for (PolicyListCountModel policyListCountModel : insurancePolicyListCountByTimeAndManagerUuidAndProductId) {
                map.put(policyListCountModel.product_id, policyListCountModel);
            }

            for (String productId : custWarrantyPolicyholderCountBean.productIds) {
                PolicyListCountModel policyListCountModel = map.get(productId);
                PolicyholderCountBean policyholderCountBean;
                if (policyListCountModel == null) {
                    policyholderCountBean = new PolicyholderCountBean(productId, 0);
                } else {
                    policyholderCountBean = new PolicyholderCountBean(policyListCountModel);
                }
                result.add(policyholderCountBean);
            }

        }
        return result;
    }


    @Override
    public int getPolicyCountByAgentStatus(InsurancePolicyModel search) {
        return insurancePolicyDao.findCountByAUuidWarrantyStatus(search);
    }

    @Override
    public int getPolicyCountByCostStatus(InsurancePolicyModel search) {
        return insurancePolicyDao.findCountByAUuidCostStatus(search);
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

        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
        insurancePolicyModel.page = setPage(insuranceRecord.lastId, insuranceRecord.pageNum, insuranceRecord.pageSize);

        insurancePolicyModel.manager_uuid = insuranceRecord.managerUuid;
        insurancePolicyModel.person_type = insuranceRecord.personType;
        insurancePolicyModel.card_code = insuranceRecord.cardCode;
        insurancePolicyModel.card_type = insuranceRecord.cardType;

        List<InsurancePolicyModel> insuranceRecordListByManagerUuid = insurancePolicyDao.findInsuranceRecordListByManagerUuid(insurancePolicyModel);
        long total = insurancePolicyDao.findInsuranceRecordCountByManagerUuid(insurancePolicyModel);

        int listSize = 0;
        insuranceRecord.data = new ArrayList<>();

        if (insuranceRecordListByManagerUuid != null && !insuranceRecordListByManagerUuid.isEmpty()) {
            for (InsurancePolicyModel policyModel : insuranceRecordListByManagerUuid) {

                List<InsuranceParticipantModel> insuranceParticipantInsuredByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantInsuredByWarrantyUuid(policyModel.warranty_uuid);

                if (insuranceParticipantInsuredByWarrantyUuid != null && !insuranceParticipantInsuredByWarrantyUuid.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    int size = insuranceParticipantInsuredByWarrantyUuid.size();
                    for (int i = 0; i < size; i++) {
                        InsuranceParticipantModel insuranceParticipantModel = insuranceParticipantInsuredByWarrantyUuid.get(i);
                        sb.append(insuranceParticipantModel.name);

                        if (StringKit.isEmpty(insuranceParticipantModel.relation_name)) {
                            sb.append("(");
                            sb.append(insuranceParticipantModel.relation_name);
                            sb.append(")");
                        }

                        if (i != size - 1) {
                            sb.append(",");
                        }
                    }
                    policyModel.insured_name = sb.toString();
                }

                InsuranceParticipantModel policyHolderNameAndMobileByWarrantyUuid = insuranceParticipantDao.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(policyModel.warranty_uuid);

                InsurancePolicy.GetInsurancePolicy insurancePolicy = new InsurancePolicy.GetInsurancePolicy(policyModel);

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
