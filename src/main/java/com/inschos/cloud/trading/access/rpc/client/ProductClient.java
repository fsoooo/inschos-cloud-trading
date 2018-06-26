package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.service.ProductService;
import com.inschos.cloud.trading.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:53
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class ProductClient {

    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/api/rpc/product";


    private ProductService getService() {
        return new HproseHttpClient(host + uri).useService(ProductService.class);
    }


    public List<ProductBean> getPlatformProductAll(String managerUuid, int categoryId) {
        try {
            ProductService service = getService();
            return service != null ? service.getPlatformProductAll(managerUuid, categoryId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductBean getProduct(long productId) {
        try {
            ProductService service = getService();
            return service != null ? service.getProduct(productId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductBrokerageInfoBean getBrokerage(ProductBrokerageBean search) {
        try {
            ProductService service = getService();
            return service != null ? service.getBrokerage(search) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductBean> getProductList(List<String> ids) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductList(ids) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductCategory> getCategoryList(String level) {
        try {
            ProductService service = getService();
            return service != null ? service.getCategoryList(level) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<InsuranceCo> getProductCoList(String managerUuid) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductCoList(managerUuid) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<InsuranceCompanyBean> getListInsuranceCompany(InsuranceCompanyBean insuranceCompanyBean) {
        try {
            ProductService service = getService();
            return service != null ? service.getListInsuranceCompany(insuranceCompanyBean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductBean> getListProduct(String name, String managerUuid) {
        try {
            ProductService service = getService();
            return service != null ? service.getListProduct(name, managerUuid) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public InsuranceCompanyBean getCompany(long id) {
        try {
            ProductService service = getService();
            return service != null ? service.getCompany(id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<InsuranceCompanyBean> getCompanyList(List<String> companyId) {
        try {
            ProductService service = getService();
            return service != null ? service.getCompanyList(companyId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
