package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.InsuranceCompanyBean;
import com.inschos.cloud.trading.access.rpc.service.CompanyService;
import com.inschos.common.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/6/27 on 15:17
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CompanyClient {

    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/api/rpc/company";


    private CompanyService getService() {
        return new HproseHttpClient(host + uri).useService(CompanyService.class);
    }


    public List<InsuranceCompanyBean> getListInsuranceCompany(InsuranceCompanyBean insuranceCompanyBean) {
        try {
            CompanyService service = getService();
            return service != null ? service.getListInsuranceCompany(insuranceCompanyBean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public InsuranceCompanyBean getCompany(long id) {
        try {
            CompanyService service = getService();
            return service != null ? service.getCompany(id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<InsuranceCompanyBean> getCompanyList(List<String> companyId) {
        try {
            CompanyService service = getService();
            return service != null ? service.getCompanyList(companyId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<InsuranceCompanyBean> getCompanyAll(){
        try {
            CompanyService service = getService();
            return service != null ? service.getCompanyAll() : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }
}
